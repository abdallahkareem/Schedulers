
import java.util.*;

public class AGScheduler implements Scheduler {

    private List<Process> executionOrder = new ArrayList<>();
    private List<Process> finished = new ArrayList<>();

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    public List<Process> getFinishedProcesses() {
        return finished;
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {

        List<Process> notArrived = new ArrayList<>(processes);
        notArrived.sort(Comparator.comparingInt(p -> p.arrival));

        Queue<Process> ready = new LinkedList<>();

        int time = 0;
        int completed = 0;
        while (completed < processes.size()) {

            while (!notArrived.isEmpty() && notArrived.get(0).arrival <= time)
                ready.add(notArrived.remove(0));

            if (ready.isEmpty()) {
                time++;
                continue;
            }

            Process current = ready.remove();
            recordExecution(current);

            int q = current.quantum;
            int usedQuantumAmount = 0;

            /* ===== Phase 1: FCFS (25%) ===== */
            int phase1 = (int) Math.ceil(q * 0.25);

            while (usedQuantumAmount < phase1 && current.remaining > 0) {
                executeOneUnit(current);
                time++;
                usedQuantumAmount++;
                addArrivals(notArrived, ready, time);
            }

            if (finish(current, time)) {
                completed++;
                continue;
            }

            if (existsHigherPriority(current, ready)) {
                updateQuantumPriority(current, q, usedQuantumAmount);
                ready.add(current);
                continue;
            }

            /* ===== Phase 2: preemtive Priority (25%) ===== */
            int phase2 = (int) Math.ceil(q * 0.25);

            while (usedQuantumAmount < phase1 + phase2 && current.remaining > 0) {
                executeOneUnit(current);
                time++;
                usedQuantumAmount++;
                addArrivals(notArrived, ready, time);

                if (existsHigherPriority(current, ready)) {
                    updateQuantumPriority(current, q, usedQuantumAmount);
                    ready.add(current);
                    break;
                }
            }

            if (finish(current, time)) {
                completed++;
                continue;
            }

            if (existsShorter(current, ready)) {
                updateQuantumSJF(current, q, usedQuantumAmount);
                ready.add(current);
                continue;
            }

            /* ===== Phase 3: SJF (50%) ===== */
            while (usedQuantumAmount < q && current.remaining > 0) {
                executeOneUnit(current);
                time++;
                usedQuantumAmount++;
                addArrivals(notArrived, ready, time);
            }

            if (finish(current, time)) {
                completed++;
                continue;
            }

            /* ===== Quantum exhausted ===== */
            current.quantum += 2;
            current.quantumHistory.add(current.quantum);
            ready.add(current);
        }
    }

    private void executeOneUnit(Process p) {
        p.remaining--;
    }

    private void addArrivals(List<Process> notArrived, Queue<Process> ready, int time) {
        while (!notArrived.isEmpty() && notArrived.get(0).arrival <= time) {
            ready.add(notArrived.remove(0));
        }
    }

    private boolean finish(Process p, int time) {
        if (p.remaining == 0 && p.completionTime == -1) {
            p.completionTime = time;
            p.turnaround = time - p.arrival;
            p.waiting = p.turnaround - p.burst;

            if (p.quantumHistory.isEmpty() || p.quantumHistory.get(p.quantumHistory.size()-1) != 0) {
                p.quantumHistory.add(0);
            }

            finished.add(p);
            return true;
        }
        return false;
    }

    private void recordExecution(Process p) {
        if (executionOrder.isEmpty()
                || executionOrder.get(executionOrder.size() - 1) != p) {
            executionOrder.add(p);
        }
    }

    private boolean existsHigherPriority(Process current, Queue<Process> ready) {
        Process highest = current;
        for (Process p : ready)
            if (p.priority < current.priority)
                current = p;
        if (highest != current) {
            return true;
        }
        return false;
    }

    private boolean existsShorter(Process current, Queue<Process> ready) {
        Process shortest = current;
        for (Process p : ready)
            if (p.remaining < current.remaining)
                current = p;
        if (shortest != current) {
            return true;
        }
        return false;
    }

    private void updateQuantumPriority(Process p, int q, int usedQuantumAmount) {
        p.quantum = q + (int) Math.ceil((q - usedQuantumAmount) / 2.0);
        p.quantumHistory.add(p.quantum);
    }

    private void updateQuantumSJF(Process p, int q, int usedQuantumAmount) {
        p.quantum = q + (q - usedQuantumAmount);
        p.quantumHistory.add(p.quantum);
    }
}
