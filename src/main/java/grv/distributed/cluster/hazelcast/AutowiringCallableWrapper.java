package grv.distributed.cluster.hazelcast;

import grv.distributed.instruction.Instruction;
import com.hazelcast.spring.context.SpringAware;
import java.io.Serializable;
import java.util.concurrent.Callable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A wrapper for an {@link Instruction} that adds the Hazelcast annotation to
 * perform Spring bean injection and apply *Aware methods.
 *
 * @param <T> Return type of the instruction.
 */
@SpringAware
public class AutowiringCallableWrapper<T>
    implements Serializable, Callable<T>, ApplicationContextAware {

  private static final long serialVersionUID = 7735579970328046975L;

  /**
   * Wrapped instruction instance.
   */
  private final Instruction<? extends T> instruction;

  /**
   * Constructor.
   *
   * @param instruction Instruction to wrap.
   */
  public AutowiringCallableWrapper(Instruction<? extends T> instruction) {
    this.instruction = instruction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T call() throws Exception {
    return instruction.call();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(instruction);
    beanFactory.initializeBean(instruction, instruction.getClass().getName());
  }
}
