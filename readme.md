# Distributed Scheduler
A distributed task scheduler capable of executing long running tasks by
dividing them into multiple smaller unit of work and executing them parallely on
multiple nodes.

# Features!
Scheduler is capable of the following:
- Distributed - Ability to run on multiple nodes
- Fault tolerant - If a node goes down, the jobs is not impacted and resumes from the last known checkpoint.
- Parallel - The scheduler has ability to run multiple jobs at the same time with minimal wait time.
- Horizontal scale - The scheduler is capable of scaling up or scaling down horizontally
### How to start the appication?
Build the application
```sh
$ mvn clean package
```
Start the appication
```sh
$ java -Dserver.port=<port> -jar <jar-name>
```
### How to use?
Check the report of all the nodes and their workloads. (One can send the request to any node)
```
http://localhost:<port>/
```
Exectute a task immediately.
```
http://localhost:<port>/execute?begin=<start>&end=<end>
```
Schedule a task.
```
http://localhost:8080/schedule?begin=<start>&end=<end>&cron=<cron_expression>
```
Note: The scheduler will fail to print the number 9. This is coded specifically to test the cases where a task fails. 

### Todos
 - Write Tests


