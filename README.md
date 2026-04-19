
# 🚀 Ultra-Low-Latency Order Matching Engine (Java)

A high-performance, lock-free, multi-threaded order matching engine built from scratch in Java, designed to simulate the core infrastructure used in real-world trading systems and high-frequency event pipelines.

---

# 🧠 Why This Project Exists

Most backend systems rely on blocking abstractions and frameworks.

This project intentionally avoids those to:

- Understand how hardware affects software
- Explore lock-free data structures
- Measure real system latency
- Optimize through evidence, not assumptions
- Build systems-level engineering intuition

This project is not just an implementation — it is a **performance engineering journey**.

---

# 🏗️ System Architecture

![System Architecture](architecture.png)

High-Level Flow:

Multiple Producers  
↓  
Lock-Free Ring Buffer (MPSC)  
↓  
Single Matching Engine  
↓  
Order Book (Priority Queues)

Design Principles:

- Multiple writers
- Single consumer
- No locks in matching path
- Minimal contention
- Cache-aware memory layout

---

# 📦 Core Components

## Order

Represents a market order.

Fields:

- orderId
- price
- quantity
- timestamp
- orderType
- createdTime

Responsibilities:

- Support partial fills
- Track remaining quantity
- Maintain creation timestamp
- Enable latency measurement

---

## OrderBook

Maintains:

- Buy Orders → Max Heap
- Sell Orders → Min Heap

Matching Rule:

BUY.price ≥ SELL.price

Characteristics:

- FIFO within same price
- Partial matching supported
- Continuous matching loop

Design Choice:

PriorityQueue used for:

- O(log N) insert
- O(log N) removal
- Efficient price ordering

---

## RingBuffer (Custom Lock-Free Queue)

Multi-Producer Single-Consumer (MPSC) queue implemented using:

Compare-And-Swap (CAS)

Key Features:

- Lock-free publishing
- Bitmask indexing (no modulo)
- Busy-wait consumer
- False-sharing prevention
- Cache-friendly layout

Replaced:

ArrayBlockingQueue

Reason:

Blocking queues introduce kernel scheduling overhead.

---

## MatchingEngine

Single-threaded consumer responsible for:

- Reading orders
- Matching orders
- Recording latency
- Tracking throughput

Why Single Thread?

Avoids locking inside OrderBook.

---

## Producer

Responsibilities:

- Generate synthetic load
- Publish orders continuously
- Simulate real traffic

Multiple producers run concurrently.

---

# ⚙️ Optimization Journey

## Stage 1 — Baseline Matching Engine

Initial implementation:

- OrderBook
- PriorityQueue Matching
- Single-threaded execution

Focus:

Correctness first

---

## Stage 2 — Multi-Threaded Producers

Introduced:

- Multiple producer threads
- BlockingQueue communication

Problem:

Blocking queues limited throughput.

---

## Stage 3 — Lock-Free RingBuffer

Replaced:

BlockingQueue → Custom RingBuffer

Implemented:

- CAS-based publish
- Busy-wait consume
- Power-of-two indexing

Result:

Major throughput improvement.

---

## Stage 4 — Cache Optimization

Implemented:

- False-sharing padding
- Separated read/write indexes

Result:

Reduced cache contention and latency jitter.

---

## Stage 5 — Producer Scaling Analysis

Tested:

1 → 6 producer threads

Observation:

Peak performance at 3 producers.

Reason:

3 producers + 1 consumer = 4 CPU cores.

More producers caused:

- Context switching
- CAS contention
- Performance regression

---

## Stage 6 — Latency Instrumentation

Implemented:

- End-to-End latency tracking
- Processing latency tracking
- Percentile calculation

Measured:

- P50
- P95
- P99

This enabled real bottleneck discovery.

---

# 📊 Benchmark Methodology

Benchmarks were performed using:

- Continuous synthetic load
- Fixed-duration runs
- Latency percentile tracking
- Produced vs Consumed validation

Duration:

60 seconds sustained load

This exposes:

- CPU throttling
- Scheduling contention
- Long-term stability

---

# 🧪 Hardware Environment

CPU: Intel i5-7300HQ (4 cores)  
RAM: 8 GB  
Java: 17 LTS  
OS: Windows 10

---

# 📈 Final Performance Results

Configuration:

Producers: 3  
RingBuffer Size: 8192  
Runtime: 60 seconds

Results:

Total Trades:

58,327,987

Throughput:

971,801 trades/sec

Latency:

P50: 5602 ns  
P95: 7340 ns  
P99: 23050 ns

Processing Latency:

P50: 300 ns  
P95: 1600 ns  
P99: 2300 ns

---

# 📉 Producer Scaling Results

Producers | Throughput
-----------|-------------
1 | ~600k/sec
2 | ~1.2M/sec
3 | ~1.3M/sec (Optimal)
4 | Plateau
6 | Regression

Key Insight:

Thread count must match CPU cores.

---

# 🔍 Engineering Insights

## Insight 1 — Matching Is Not the Bottleneck

Processing ≈ 300 ns  
Total latency ≈ 5600 ns

Most time spent waiting, not matching.

---

## Insight 2 — Lock-Free Design Matters

Replacing BlockingQueue:

- Major throughput increase
- Lower latency jitter

---

## Insight 3 — CPU Cache Behavior Matters

False-sharing caused:

Latency instability.

Padding fixed:

Cache contention.

---

## Insight 4 — Scaling Has Limits

More threads does not always mean faster.

Hardware limits exist.

---

# 📌 Future Improvements

## Multi-Symbol Matching

Partition system:

Symbol → Dedicated OrderBook

Benefit:

Parallel matching pipelines.

---

## Object Pooling

Reuse Order objects.

Benefit:

Reduced allocation pressure.

---

## Async Trade Logging

Persist trades asynchronously.

Possible targets:

- Kafka
- Elasticsearch
- File WAL

---

## Custom Heap Implementation

Replace PriorityQueue with:

Price-level buckets.

---

## Thread Affinity

Pin threads to CPU cores.

Benefit:

Better cache locality.

---

# ▶️ How to Run

Compile:

javac *.java

Run:

java Main

---

# 🧠 Skills Demonstrated

- Lock-free concurrency
- Multi-threaded system design
- Latency measurement
- Performance optimization
- CPU-aware engineering
- Benchmark-driven development

---

# ⭐ Final Summary

This project demonstrates how a simple system evolves into a high-performance concurrent engine through careful measurement, architectural changes, and hardware-aware optimizations.

It reflects real engineering trade-offs and performance reasoning.
