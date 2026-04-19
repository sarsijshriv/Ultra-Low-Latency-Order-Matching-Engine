
# Ultra-Low Latency Order Matching Engine (Java)

A high-performance, multi-threaded order matching engine built in Java to simulate real-world exchange-style order processing pipelines.

This project focuses on **throughput, latency measurement, and concurrency design**, not just correctness.

---

# 🚀 Performance Snapshot

**Test Duration:** 10 seconds  
**Producer Mode:** `Thread.sleep(0)`  
**Architecture:** Multi-producer → Single-consumer

## Throughput

```
Total Trades:        4,184,168
Throughput:          417,953 trades/sec
```

## End-to-End Latency

```
P50:   1,509 µs
P95:   2,276 µs
P99:  23,876 µs
```

## Processing Latency

```
P50:     500 µs
P95:   2,900 µs
P99:   4,400 µs
```

**Key Observation**

Most latency is dominated by **queue wait time**, not computation time — indicating that the single-consumer pipeline is the primary throughput boundary.

---

# 📌 Overview

This system simulates a simplified financial order matching engine using:

- Multi-threaded producers
- Single-threaded matching consumer
- Priority-based order matching
- Latency and throughput measurement
- Realistic queue-driven pipeline architecture

The goal of this project is to understand how **high-throughput systems behave under load**, and how architectural decisions impact performance.

---

# 🧠 System Architecture

```
Producers (multi-threaded)
        ↓
BlockingQueue (thread-safe)
        ↓
Matching Engine (single consumer)
        ↓
OrderBook (priority-based matching)
```

## Key Design Decisions

- **Single consumer** avoids synchronization overhead
- **BlockingQueue** ensures safe inter-thread communication
- **PriorityQueue** enables efficient price-based matching
- **Latency instrumentation** enables performance diagnostics

---

# ⚙️ Matching Logic

Orders are matched based on:

1. Price priority
2. Time priority (FIFO within same price)

## Rules

- Buy orders use **max-heap**
- Sell orders use **min-heap**
- A match occurs when:

```
buyPrice >= sellPrice
```

Trade quantity:

```
min(buyQty, sellQty)
```

---

# 📊 Latency Types Measured

## End-to-End Latency

```
Order creation → Order match
```

Includes:

- Queue wait time
- Matching wait time
- Processing time

---

## Processing Latency

```
Time spent inside addOrder()
```

Includes:

- Heap operations
- Matching logic
- Order updates

Excludes:

- Queue wait time

---

# 🧰 Tech Stack

- Java (JDK 17+ recommended)
- java.util.concurrent
- PriorityQueue
- BlockingQueue
- Lombok (optional)
- JUnit (planned)

---

# 📂 Project Structure

```
src/

OrderType.java
    Enum representing BUY and SELL orders

Order.java
    Core order model with price, quantity, timestamp

Trade.java
    Trade execution model

OrderBook.java
    Maintains buy/sell priority queues
    Handles order matching

MatchingEngine.java
    Consumer thread
    Processes incoming orders
    Measures processing latency

Producer.java
    Generates orders concurrently

Main.java
    System orchestration
    Performance measurement
```

---

# 🎯 Why Single Consumer?

Multiple consumers would require:

- Locks
- Synchronization
- Contention handling

Instead:

```
Many Producers → One Consumer
```

## Benefits

- Predictable latency
- Reduced lock overhead
- Deterministic processing order

This pattern is widely used in **low-latency event-driven systems**.

---

# ▶️ How to Run

Compile:

```
javac *.java
```

Run:

```
java Main
```

Expected output:

```
Total Trades: XXXXX
Throughput: XXXXX trades/sec

End-to-End P50: ...
End-to-End P95: ...
End-to-End P99: ...

Processing P50: ...
Processing P95: ...
Processing P99: ...
```

---

# 🔧 Load Configuration

Inside:

```
Producer.java
```

Control load using:

```java
Thread.sleep(0);
```

Options:

```
sleep(1) → low load  
sleep(0) → moderate load  
no sleep → maximum load  
```

---

# ⚠️ Current Limitations

- Trade objects stored in memory (optimization planned)
- No batching yet
- No persistence layer
- No distributed partitioning
- No network interface

---

# 🔄 Planned Improvements (Stage 4)

- Replace Trade list with lightweight counters
- Reduce object allocation
- Introduce batching
- Improve memory locality
- Reduce GC pressure
- Optimize latency distribution

Expected outcomes:

- Higher throughput
- Lower P99 latency
- Reduced memory overhead

---

# 🌐 Future Enhancements (Stage 5)

- HTTP API for order submission
- External load testing integration
- Metrics export support
- Multi-partition matching simulation
- Distributed deployment model

---

# 📚 Learning Outcomes

This project demonstrates:

- Multi-threaded system design
- Producer–consumer architecture
- Queue-driven system behavior
- Latency measurement techniques
- Throughput benchmarking
- Bottleneck identification
- Performance-oriented design

---

# 💻 Hardware Used

```
CPU: Intel i5-7300HQ @ 2.5 GHz
Cores: 4
RAM: 8 GB
Storage: ~1 TB
GPU: 4 GB (not used in computation)
Operating System: Windows 10
Java Version: 17
```
