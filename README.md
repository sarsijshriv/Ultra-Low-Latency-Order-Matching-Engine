# 🚀 Ultra-Low-Latency Order Matching Engine

High-performance lock-free order matching engine built in Java to explore low-latency systems design, mechanical sympathy, and concurrent architecture patterns used in trading infrastructure.

Designed around:
- lock-free communication
- cache-aware data structures
- predictable latency
- benchmark-driven optimization

---

# 📊 Performance

| Metric | Result |
|---|---|
| Throughput | 971K+ trades/sec |
| Runtime | 60s sustained load |
| P50 Latency | 5.6 µs |
| P95 Latency | 7.3 µs |
| P99 Latency | 23 µs |

### Test Environment

| Component | Value |
|---|---|
| CPU | Intel i5-7300HQ (4 cores) |
| RAM | 8 GB |
| Java | 17 LTS |
| OS | Windows 10 |

---

# 🏗️ Architecture

![Architecture](architecture.png)

### Pipeline

```text
Multiple Producers
        ↓
Lock-Free Ring Buffer (MPSC)
        ↓
Single Matching Engine
        ↓
Order Book
```

### Design Goals

- Multiple concurrent producers
- Single-threaded matching path
- No locks during matching
- Minimal contention
- Cache-friendly memory layout
- Predictable latency under load

---

# ⚙️ Core Components

| Component | Responsibility |
|---|---|
| `Producer` | Generates synthetic market traffic |
| `RingBuffer` | Lock-free MPSC queue using CAS |
| `MatchingEngine` | Single-threaded matching consumer |
| `OrderBook` | Maintains buy/sell priority queues |
| `Order` | Tracks order state and latency metadata |

---

# 🔧 Key Optimizations

## Lock-Free Ring Buffer

Replaced `ArrayBlockingQueue` with custom CAS-based MPSC ring buffer.

### Features
- lock-free publishing
- power-of-two indexing
- bitmask access
- busy-wait consumer
- reduced coordination overhead

### Result
- higher throughput
- lower latency jitter
- reduced scheduler interference

---

## Cache-Aware Design

Implemented:
- cache-line padding
- separated read/write indexes
- contention reduction

### Result
Lower latency variance under sustained load.

---

## Single Consumer Matching

Matching engine intentionally runs on a single thread.

### Why?

Avoids:
- locks inside order book
- synchronization overhead
- coordination complexity

Tradeoff:
- simpler deterministic matching path
- bounded scaling model

---

# 📈 Scaling Analysis

| Producers | Throughput |
|---|---|
| 1 | ~600K/sec |
| 2 | ~1.2M/sec |
| 3 | ~1.3M/sec |
| 4 | Plateau |
| 6 | Regression |

### Observation

Optimal performance occurred at 3 producers on a 4-core CPU.

Beyond that:
- CAS contention increased
- context switching increased
- throughput regressed

This highlighted hardware-aware scaling limits.

---

# 🧪 Benchmark Methodology

Benchmarks were executed using:
- continuous synthetic load
- sustained 60-second runs
- percentile latency tracking
- producer vs consumer validation

Measured:
- throughput
- end-to-end latency
- processing latency
- percentile distribution (P50/P95/P99)

---

# 🔍 Engineering Insights

## Matching Was Not the Bottleneck

| Metric | Approx |
|---|---|
| Processing Latency | ~300 ns |
| End-to-End Latency | ~5600 ns |

Most latency originated from coordination and queueing rather than matching itself.

---

## Lock-Free Structures Matter

Replacing blocking queues significantly improved:
- throughput
- latency stability
- contention behavior

---

## More Threads ≠ Better Performance

Performance peaked near hardware core limits.

Additional threads introduced:
- contention
- scheduling overhead
- reduced efficiency

---

# 📌 Current Limitations

This project intentionally focuses on matching-path performance and concurrency behavior.

It does not yet model:
- order cancellation
- persistence/recovery
- replay logs
- market orders
- risk management
- distributed matching
- deterministic replication

---

# 🛣️ Future Improvements

## Multi-Symbol Parallelism
Partition matching by symbol for parallel execution.

## Object Pooling
Reduce allocation pressure and GC overhead.

## Async Trade Logging
Persist trades asynchronously using Kafka/WAL pipelines.

## Custom Price-Level Structures
Replace `PriorityQueue` with optimized price buckets.

## Thread Affinity
Pin threads to CPU cores for improved cache locality.

---

# ▶️ Running

## Compile

```bash
javac *.java
```

## Run

```bash
java Main
```

---

# 🧠 Concepts Demonstrated

- Lock-free concurrency
- CAS synchronization
- MPSC queue design
- Cache-aware engineering
- JVM performance reasoning
- Latency instrumentation
- Throughput benchmarking
- Concurrent systems design
- Mechanical sympathy

---

# 📚 Project Goal

This project was built to understand how architectural decisions, CPU behavior, synchronization strategies, and memory access patterns affect real-world system latency and throughput.

It focuses on measurable engineering tradeoffs rather than framework-heavy abstractions.
