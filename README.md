# 🚀 Ultra-Low Latency Order Matching Engine (Java)

A high-performance, multi-threaded **price-time priority order matching
engine** built from scratch in Java and optimized through systematic
performance engineering.

------------------------------------------------------------------------

# 📌 Project Overview

Core pipeline:

Multiple Producers → ArrayBlockingQueue → MatchingEngine → OrderBook →
Trade Execution

Design goals:

-   Ultra-low latency execution
-   High throughput matching
-   Deterministic price-time priority
-   Minimal memory allocation
-   Measurable performance tuning

------------------------------------------------------------------------

# ⚙️ System Architecture

Producer #1\
Producer #2\
↓\
ArrayBlockingQueue (Capacity: 1000)\
↓\
Matching Engine (Single Consumer)\
↓\
Order Book (Buy Heap + Sell Heap)

------------------------------------------------------------------------

# 🧠 Matching Model

Rules:

-   Buy Orders → Highest price first\
-   Sell Orders → Lowest price first\
-   Same price → Earlier order first\
-   Match when Buy Price ≥ Sell Price

Data structures:

-   Buy Orders → Max Heap (PriorityQueue)
-   Sell Orders → Min Heap (PriorityQueue)

------------------------------------------------------------------------

# 📈 Performance Evolution Timeline

## Stage 1 --- Baseline

Throughput: \~417,953 trades/sec

Latency:\
P50: 1509 µs\
P95: 2276 µs\
P99: 23876 µs

Processing:\
P50: 500 µs\
P95: 2900 µs\
P99: 4400 µs

------------------------------------------------------------------------

## Stage 2 --- Memory Optimization

Throughput: \~535,000 trades/sec

Changes:

-   Removed Trade object storage
-   Replaced with trade counter

------------------------------------------------------------------------

## Stage 3 --- Primitive Latency Arrays

Throughput: \~652,000 trades/sec

Changes:

-   Replaced List`<Long>`{=html} with long\[\]
-   Removed boxing overhead

------------------------------------------------------------------------

## Stage 4 --- Comparator Optimization

Throughput: \~705,000 trades/sec

Changes:

-   Inline comparator logic
-   Reduced heap comparison overhead

------------------------------------------------------------------------

## Stage 5 --- Final Optimized Engine

Final Performance:

Throughput: **\~764,251 trades/sec**

Latency:

P50: 863 µs\
P95: 1270 µs\
P99: 9930 µs

Processing:

P50: 100 µs\
P95: 1100 µs\
P99: 1700 µs

------------------------------------------------------------------------

# 📊 Final Performance Summary

Test Environment:

CPU: Intel i5-7300HQ @ 2.5 GHz\
Cores: 4\
RAM: 8 GB\
OS: Windows 10\
Java: 17.0.8 LTS

Queue Type: ArrayBlockingQueue\
Queue Capacity: 1000\
Producer Threads: 2\
Benchmark Duration: 10 seconds

------------------------------------------------------------------------

# 📉 Net Improvements

Throughput:

417k → 764k trades/sec\
≈ **+83% improvement**

P99 Latency:

23876 µs → 9930 µs\
≈ **−58% reduction**

Processing P99:

4400 µs → 1700 µs\
≈ **−61% reduction**

------------------------------------------------------------------------

# 🧪 Testing Methodology

-   Continuous order generation
-   Multi-producer load
-   10-second benchmark window
-   Latency percentile tracking

Metrics recorded:

-   Throughput
-   P50 latency
-   P95 latency
-   P99 latency
-   Processing latency

------------------------------------------------------------------------

# 🚧 Future Enhancements

Planned:

-   Replace ArrayBlockingQueue with RingBuffer
-   Multi-order-book partitioning
-   Parallel matching engines
-   Lock-free routing

Target:

**1,000,000+ trades/sec**

------------------------------------------------------------------------

# 🏁 Current Capability

\~764,000 trades/sec\
\~863 µs P50 latency\
\~9.9 ms P99 latency\
Single matching thread\
Commodity laptop hardware

Achieved through:

-   Heap optimization
-   Memory-aware design
-   Hot-path tuning
-   Comparator optimization
-   Latency instrumentation
