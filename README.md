# CPU Schedulers

## Overview

This project is a **Java-based CPU Scheduling Simulator** developed as part of **Operating System I – Assignment #3**.

The simulator implements and compares multiple CPU scheduling algorithms while considering **context switching**, **arrival time**, **burst time**, **priority**, and **quantum handling**.

---

## 🧠 Implemented Scheduling Algorithms

### 1️⃣ Preemptive Shortest Job First (SJF)

* Also known as **Shortest Remaining Time First (SRTF)**
* The process with the **smallest remaining burst time** is selected
* Preemption occurs when a new process arrives with a shorter remaining time
* Context switching is handled

---

### 2️⃣ Round Robin (RR)

* Each process is assigned a **time quantum**
* Processes are executed cyclically
* If a process does not finish within its quantum, it is moved to the end of the ready queue
* Context switching is applied

---

### 3️⃣ Preemptive Priority Scheduling (with starvation solution)

* Processes are scheduled based on **priority** (lower value = higher priority)
* Preemptive: a running process can be interrupted by a higher priority process
* **Starvation problem is handled** using aging or priority adjustment

---

### 4️⃣ AG Scheduling Algorithm

AG Scheduling is a **hybrid scheduling algorithm** combining:

* FCFS
* Non-preemptive Priority
* Preemptive SJF

Each process has its own **static quantum**.

#### 🔄 AG Execution Rules

For each process, execution follows these steps:

1. **First 25% of quantum → FCFS**
2. **Next 25% of quantum → Non-preemptive Priority**
3. **Remaining 50% of quantum → Preemptive SJF**

---

### AG Scheduling Scenarios

After execution, one of the following cases occurs:

1. **Process used all its quantum and still has remaining burst time**

   * Added to the end of the ready queue
   * Quantum increased by **+2**

2. **Process was interrupted during non-preemptive priority phase**

   * Added to the end of the queue
   * Quantum increased by **ceil(remaining quantum / 2)**

3. **Process was interrupted during preemptive SJF phase**

   * Added to the end of the queue
   * Quantum increased by the **remaining quantum**

4. **Process finished before quantum expired**

   * Quantum is set to **0**

---

## Input Format

Each process is defined by:

| Attribute    | Description                                  |
| ------------ | -------------------------------------------- |
| Process ID   | Unique identifier (e.g., P1, P2)             |
| Arrival Time | Time when the process enters the ready queue |
| Burst Time   | Total CPU execution time                     |
| Priority     | Process priority (lower is higher priority)  |
| Quantum      | Initial time quantum (AG Scheduling)         |

---

## Example Input

| Process | Burst Time | Arrival Time | Priority | Quantum |
| ------- | ---------- | ------------ | -------- | ------- |
| P1      | 17         | 0            | 4        | 7       |
| P2      | 6          | 2            | 7        | 9       |
| P3      | 11         | 5            | 3        | 4       |
| P4      | 4          | 15           | 6        | 6       |

---

## Output

The simulator displays:

* Execution order of processes
* Quantum updates (for AG Scheduling)
* Waiting Time
* Turnaround Time
* Average Waiting Time
* Average Turnaround Time

---


## ▶️ How to Run

```bash
javac Main.java
java Main
```

> Make sure all `.java` files are in the same directory.

---
