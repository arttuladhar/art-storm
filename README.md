### Apache Storm
Apache Storm is free and open source distributed realtime computation system. Storm provides reliable realtime data processing what Hadoop did for batch processing. It provides realtime, robust, user friendly, reliable data processing capability with operational Intelligence.

#### Use Cases of Storm
* Realtime Analytics
* Online Machine Learning
* Continuous Computation
* ETL

Apache Storm is a platform for analyzing realtime streams of data as they arrive, so you can react to data as it happens.

---
### Storm Data Model
* Spouts - Source of Data for the Topology. Postgres/MySQL/Kafka/Kestrel
* Bolts - Units of Computation of Data. Eg. - Aggregation/Filtering/Join/Transformation
* Tuple - Unit of Data entering the stream. Immutable ordered list of elements
* Topology - Directed Acyclic Graph, Vertices = Computation and Edges = Stream of Data
* Stream - Unordered sequence of tuples

#### Stream Grouping
* Shuffle Grouping
  An equal number of tuples is distributed randomly across all of the workers executing the bolts. The following diagram depicts the structure.
* Fields Grouping
  The fields with same values in tuples are grouped together and the remaining tuples kept outside. Then, the tuples with the same field values are sent forward to the same worker executing the bolts. For example, if the stream is grouped by the field “word”, then the tuples with the same string, “Hello” will move to the same worker. The following diagram shows how Field Grouping works.
* All Grouping
* Global Grouping

---
### Storm Cluster Architecture

* Storm Cluster consists of Nimbus (master node) and Supervisor (worker node).

* Nimbus is the central component of Apache Storm, which analyzes the topology and gathers the tasks to be executed and distributes the work to it's worker nodes.

* Supervisor nodes (worker node) follows instructions provided by Nimbus. A supervisor has multiple worker processes.

* Worker processes will execute the task related to specific topology. Worker process spins off executor to perform a task. A worker process will have multiple executors to perform the task. A task performs the actual data processing.

---


```java
// create the topology
TopologyBuilder builder = new TopologyBuilder();

// attach the word spout to the topology - parallelism of 10
builder.setSpout("word", new TestWordSpout(), 10);

// attach the exclamation bolt to the topology - parallelism of 3
builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("word");

// attach another exclamation bolt to the topology - parallelism of 2
builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");
```
-----

