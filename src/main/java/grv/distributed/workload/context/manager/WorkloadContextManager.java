/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grv.distributed.workload.context.manager;

import grv.distributed.SchedulerProperties;
import grv.distributed.workload.Workload;
import grv.distributed.workload.context.WorkloadContextFactory;
import grv.distributed.workload.WorkloadReport;
import grv.distributed.workload.context.WorkloadContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Responsible for containing and managing running workloads via {@link WorkloadContext workload contexts} created
 * by {@link WorkloadContextFactory workload factories}. Managing the lifecycle of a workload through the manager
 * only interacts with workload contexts running on locally running application node, and not the cluster.
 * Therefore, this class should be treated as an internal component and not interacted with directly.
 */
@Slf4j
@Service
public class WorkloadContextManager {
  /**
   * Executors service.
   */
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  /**
   * Workload context factories.
   */
  private final Map<Class<? extends Workload>, WorkloadContextFactory<? extends Workload>> workloadContextFactorieByType;

  /**
   * Scheduler properties.
   */
  private final SchedulerProperties schedulerProperties;

  /**
   * Workload contexts.
   */
  private final List<WorkloadContext> workloadContexts = Collections.synchronizedList(new ArrayList<>());

  /**
   * Constructor.
   *
   * @param workloadContextFactories The set of available workload context factories.
   * @param schedulerProperties      Scheduler properties.
   */
  @Autowired
  public WorkloadContextManager(List<WorkloadContextFactory<? extends Workload>> workloadContextFactories,
                                SchedulerProperties schedulerProperties) {
    this.workloadContextFactorieByType = workloadContextFactories.stream()
        .collect(Collectors.toMap(WorkloadContextFactory::klass, Function.identity()));
    this.schedulerProperties = schedulerProperties;
  }


  /**
   * Creates a workload report containing entries for all workloads.
   *
   * @return A complete workload report.
   */
  public WorkloadReport getWorkloadReport() {
    synchronized (this) {
      // TODO: verify that the null entry problem is resolved, and change back to the stream
      WorkloadReport report = new WorkloadReport();

      for (WorkloadContext workloadContext : workloadContexts) {
        if (workloadContext == null) {
          log.error("A workload context was null and skipped.");
          continue;
        }

        report.add(workloadContext.getWorkloadReportEntry());
      }

      return report;
//            return new WorkloadReport(workloadContexts.stream().map(WorkloadContext::getWorkloadReportEntry).collect(Collectors.toList()));
    }
  }

  /**
   * Stops the given workload.
   *
   * @param workload Workload to stop.
   * @return A future for the process of stopping the workload.
   */
  public Future stop(Workload workload) {
    return stop(getContexts(workload));
  }

  /**
   * Stops the given list of workload contexts.
   *
   * @param contexts Workload contexts to stop.
   * @return A future to track completion of the workload context.
   */
  private Future stop(List<WorkloadContext> contexts) {
    return executorService.submit(() -> {
      List<Future> futures = contexts.stream().map(this::stop).collect(Collectors.toList());

      try {
        do {
          futures.removeIf(Future::isDone);
          Thread.sleep(schedulerProperties.getActionPollInterval().toMillis());
        }
        while (futures.size() > 0);
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
        log.error("Interrupted while stopping workloads");
      }
    });
  }

  /**
   * Returns all workload contexts associated with the given workload.
   *
   * @param workload Workload to retrieve contexts for.
   * @return All workload contexts associated with the given workload.
   */
  private List<WorkloadContext> getContexts(Workload workload) {
    synchronized (this) {
      return workloadContexts.stream()
          .filter(c -> c.getWorkload().equals(workload))
          .collect(Collectors.toList());
    }
  }

  /**
   * Stops the given workload context.
   *
   * @param context Workload context to stop.
   * @return A future for the process of stopping the workload context.
   */
  private Future stop(WorkloadContext context) {
    return executorService.submit(() -> {
      log.debug("Stopping workload " + context.getWorkload().getUrn());
      context.stop();

      while (true) {
        if (context.isStopped()) {
          log.debug("Workload " + context.getWorkload().getUrn() + "has stopped");
          return;
        }
        try {
          Thread.sleep(schedulerProperties.getActionPollInterval().toMillis());
        } catch (InterruptedException ignored) {
          Thread.currentThread().interrupt();
          log.error("Interrupted while stopping workload " + context.getWorkload().getUrn());
        }
      }
    });
  }

  /**
   * Restarts the given workload.
   *
   * @param workload Workload to restart.
   * @return A future for the process of restarting the workload.
   */
  public Future restart(Workload workload) {
    return executorService.submit(() -> {
      Future future = remove(workload);

      while (!future.isDone()) {
        try {
          future.get();
        } catch (InterruptedException ignored) {
          Thread.currentThread().interrupt();
          log.error("Interrupted while restarting workload " + workload.getUrn());
          return;
        } catch (ExecutionException e) {
          log.error("Unexpected execution exception while attempting to restart workload "
              + workload.getUrn());
          return;
        }
      }

      start(workload);
    });
  }

  /**
   * Stops and removes the given workload.
   *
   * @param workload Workload to remove.
   * @return A future for the process of removing the workload.
   */
  public Future remove(Workload workload) {
    synchronized (this) {
      List<WorkloadContext> contexts = getContexts(workload);
      this.workloadContexts.removeAll(contexts);
      return stop(contexts);
    }
  }

  /**
   * Starts the given workload.
   *
   * @param workload Workload to start.
   */
  public void start(Workload workload) {
    synchronized (this) {
      WorkloadContextFactory<?> factory = workloadContextFactorieByType.get(workload.getClass());
      if (factory != null) {
        WorkloadContext workloadContext = factory.createContext(workload);
        Assert.notNull(workloadContext,
            String.format("Workload context was null for workload %s using factory %s",
                workload, factory.getClass().getName()));
        workloadContexts.add(workloadContext);
        workloadContext.start();
      }
    }
  }

  /**
   * Shuts down all workloads.
   *
   * @return A future to track its execution state.
   */
  public Future shutdown() {
    return stop(workloadContexts);
  }

  /**
   * Stops and forces an error state on the given workload.
   *
   * @param workload Workload to fail.
   */
  public void fail(Workload workload) {
    fail(getContexts(workload));
  }

  /**
   * Stops and forces an error state on the given list of workload contexts.
   *
   * @param contexts Workload contexts to fail.
   */
  private void fail(List<WorkloadContext> contexts) {
    contexts.forEach(this::fail);
  }

  /**
   * Stops and forces an error state on the given workload context.
   *
   * @param context Workload context to fail.
   */
  private void fail(WorkloadContext context) {
    context.fail();
  }

  /**
   * Returns whether the manager is service the given workload (i.e. is there at least
   * one context with the given workload).
   *
   * @param workload Workload to check.
   * @return Whether the manager is service the given workload.
   */
  public boolean isServicing(Workload workload) {
    synchronized (this) {
      return workloadContexts.stream().anyMatch(c -> c.getWorkload().equals(workload));
    }
  }
}
