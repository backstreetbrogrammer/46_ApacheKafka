# Apache Kafka

> This is a tutorial course covering Apache Kafka.

Tools used:

- JDK 8
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of contents

1. [Introduction to Message Brokers and Kafka](https://github.com/backstreetbrogrammer/46_ApacheKafka?tab=readme-ov-file#chapter-01-introduction-to-message-brokers-and-kafka)
2. [Kafka Installation and Cluster setup](https://github.com/backstreetbrogrammer/46_ApacheKafka?tab=readme-ov-file#chapter-02-kafka-installation-and-cluster-setup)
3. Kafka Producer
4. Kafka Consumer
5. Banking System Demo

---

## Chapter 01. Introduction to Message Brokers and Kafka

### Message Brokers - Overview

> A message broker (also known as an integration broker or interface engine) is an intermediary computer program module
> that translates a message from the formal messaging protocol of the sender to the formal messaging protocol of the
> receiver.

For example, a message broker may be used to manage a workload queue or message queue for multiple receivers, providing
reliable storage, guaranteed message delivery and perhaps transaction management.

**Key points:**

- Intermediary software (middleware) that passes messages between senders / producers and receivers / consumers
- May provide additional capabilities like:
    - Data Transformation
    - Validation
    - Queuing
    - Routing
- Full decoupling between senders and receivers

The following represent other examples of actions that might be handled by the broker:

- Route messages to one or more destinations
- Transform messages to an alternative representation
- Perform message aggregation, decomposing messages into multiple messages and sending them to their destination,
  then recomposing the responses into one message to return to the user
- Interact with an external repository to augment a message or store it
- Invoke web services to retrieve data
- Respond to events or errors
- Provide content and topic-based message routing using the publish–subscribe pattern

Message brokers are generally based on one of two fundamental architectures:

- **hub-and-spoke**: a central server acts as the mechanism that provides integration services
- **message bus**: the message broker is a communication backbone or distributed service that acts on the bus

Additionally, a more scalable multi-hub approach can be used to integrate multiple brokers.

### Kafka - Overview

> Apache Kafka is a distributed event store and stream-processing platform.

![KafkaOverview](KafkaOverview.PNG)

It is an open-source system developed by the Apache Software Foundation written in Java and Scala.

The project aims to provide a unified, high-throughput, low-latency platform for handling real-time data feeds.

Kafka can connect to external systems (for data import/export) via `Kafka Connect`, and provides the `Kafka Streams`
libraries for stream processing applications.

Kafka uses a binary TCP-based protocol optimized for efficiency and relies on a **"message set"** abstraction that
naturally **groups** messages together to reduce the overhead of the network round-trip.

This leads to larger network packets, larger sequential disk operations, contiguous memory blocks which allow Kafka to
turn a stream bursts of random messages writes into linear writes.

Apache Kafka is based on the **commit log**, and it allows users to subscribe to it and publish data to any number of
systems or real-time applications.

A **commit** is the making of a set of tentative changes **permanent**, marking the end of a **transaction** and
providing **Durability** to **ACID** transactions. The **record** of commits is called the **commit log**.

![KafkaArchitecture](KafkaArchitecture.PNG)

Kafka stores **key-value** messages that come from arbitrarily many processes called **producers**.

![PublisherRecord](PublisherRecord.PNG)

The data can be partitioned into different **"partitions"** within different **"topics"**.

Within a **partition**, messages are strictly ordered by their **offsets** (the position of a message within a
partition), and indexed and stored together with a timestamp.

![KafkaProducer](KafkaProducer.PNG)

Other processes called **"consumers"** can read messages from partitions.

![Consumers](Consumers.PNG)

For **stream processing**, Kafka offers the **Streams API** that allows writing Java applications that consume data from
Kafka and write results back to Kafka.

Kafka runs on a **cluster** of one or more **servers** (called **brokers**), and the partitions of all topics are
distributed across the cluster nodes.

Additionally, partitions are **replicated** to multiple brokers. This architecture allows Kafka to deliver massive
streams of messages in a **fault-tolerant** fashion and has allowed it to replace some of the conventional messaging
systems like Java Message Service (JMS), Advanced Message Queuing Protocol (AMQP), etc.

Kafka offers **transactional writes**, which provide **exactly-once** stream processing using the Streams API.

Kafka supports two types of topics: **Regular** and **compacted**.

**Regular topics** can be configured with a **retention time** or a **space bound**. If there are records that are older
than the specified retention time or if the space bound is exceeded for a partition, Kafka is allowed to **delete** old
data to free storage space.

By default, topics are configured with a retention time of **7 days**, but it's also possible to store data
indefinitely.

For **compacted topics**, records don't expire based on time or space bounds. Instead, Kafka treats later messages as
updates to earlier messages with the same key and guarantees never to delete the latest message per key.

Users can delete messages entirely by writing a so-called **tombstone message** with **null-value** for a specific key.

There are five major APIs in Kafka:

- **Producer API** – Permits an application to publish streams of records.
- **Consumer API** – Permits an application to subscribe to topics and processes streams of records.
- **Connect API** – Executes the reusable producer and consumer APIs that can link the topics to the existing
  applications.
- **Streams API** – This API converts the input streams to output and produces the result.
- **Admin API** – Used to manage Kafka topics, brokers, and other Kafka objects.

The consumer and producer APIs are decoupled from the core functionality of Kafka through an underlying messaging
protocol. This allows writing compatible API layers in any programming language that are as efficient as the Java APIs
bundled with Kafka. The Apache Kafka project maintains a list of such third party APIs.

**Key points**

- Apache Kafka is a highly scalable and high performance streaming messaging system
- **Producer Record**: **Key**, **Value** (our message payload), **Metadata** (**Timestamp**/**Topic**/**Partition**)
- **Kafka Topic**: Category of events/messages that consists of **Partitions**
- **Partitions**
    - each partition is an ordered queue
    - each record in a partition has an offset number
    - the **number** of partitions in a topic is directly proportional to the maximum unit of parallelism for a
      Kafka **Topic**
    - we can estimate the right number of partitions for a topic given expected our peak message rate/volume
    - by adding more message **brokers**, we can increase the Kafka topics capacity, transparently to the publishers
    - thus, we can have many broker instances working in parallel to handle incoming messages
    - and, we can have many consumers consuming in parallel from the same topic
- **Single Consumer Group**
    - messages from a topic are load balanced among consumers within a single **consumer group**
    - it's like a **distributed queue**
- Consumers in different **Consumer Groups**
    - messages from a topic are broadcast to all consumer groups
    - it's like a **Publish/Subscribe** system
- **Fault tolerance**
    - each Kafka **topic** is configured with a **replication factor**
    - a replication factor of **N** means each **partition** is replicated to **N** Kafka **brokers**
    - for each **partition**, only one broker acts as a partition **leader**, while other brokers are partition
      **followers**
    - the **leader** takes all the reads and writes
    - the **followers** replicate the partition data to stay in sync with the **leader**
- **Zookeeper**
    - Kafka is using Zookeeper for all its coordination logic
    - Kafka is using Zookeeper as a registry for **brokers** as well as for monitoring and failure detection
      (ephemeral znodes, watchers, etc.)
- **Data Persistence**
    - Kafka persists all its messages to disk
    - Even after messages are consumed by the consumers, the records still stay within Kafka for the configurable
      period of time
    - This allows new consumers to join and consume older messages
    - Consumers that failed in the process of reading/processing a message can retry
    - Failed brokers can recover very fast

---

## Chapter 02. Kafka Installation and Cluster setup

