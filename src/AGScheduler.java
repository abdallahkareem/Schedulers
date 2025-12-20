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

            // add arrivals
            while (!notArrived.isEmpty() && notArrived.get(0).arrival <= time) {
                ready.add(notArrived.remove(0));
            }

            if (ready.isEmpty()) {
                time++;
                continue;
            }

            Process current = ready.poll();
            recordExecution(current);

            int q = current.quantum;
            int used = 0;

            /* ===== Phase 1: FCFS (25%) ===== */
            int phase1 = (int) Math.ceil(q * 0.25);

            while (used < phase1 && current.remaining > 0) {
                executeOneUnit(current);
                time++;
                used++;
                addArrivals(notArrived, ready, time);
            }

            if (finish(current, time)) {
                completed++;
                time += contextSwitch;
                continue;
            }

            if (existsHigherPriority(current, ready)) {
                updateQuantumFCFS(current, q, used);
                ready.add(current);
                time += contextSwitch;
                continue;
            }

            /* ===== Phase 2: Priority (25%) ===== */
            int phase2 = (int) Math.ceil(q * 0.25);

            while (used < phase1 + phase2 && current.remaining > 0) {
                executeOneUnit(current);
                time++;
                used++;
                addArrivals(notArrived, ready, time);
            }

            if (finish(current, time)) {
                completed++;
                time += contextSwitch;
                continue;
            }

            if (existsShorter(current, ready)) {
                updateQuantumFull(current, q, used);
                ready.add(current);
                time += contextSwitch;
                continue;
            }

            /* ===== Phase 3: SJF (50%) ===== */
            while (used < q && current.remaining > 0) {
                executeOneUnit(current);
                time++;
                used++;
                addArrivals(notArrived, ready, time);

                if (existsShorter(current, ready)) {
                    updateQuantumFull(current, q, used);
                    ready.add(current);
                    break;
                }
            }

            if (finish(current, time)) {
                completed++;
                time += contextSwitch;
                continue;
            }

            /* ===== Quantum exhausted ===== */
            current.quantum += 2;
            current.quantumHistory.add(current.quantum);
            ready.add(current);
            time += contextSwitch;
        }
    }

    /* ================= Helpers ================= */

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

            // If last executed quantum is not 0, append 0
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
        for (Process p : ready)
            if (p.priority < current.priority)
                return true;
        return false;
    }

    private boolean existsShorter(Process current, Queue<Process> ready) {
        for (Process p : ready)
            if (p.remaining < current.remaining)
                return true;
        return false;
    }

    private void updateQuantumFCFS(Process p, int q, int used) {
        p.quantum = q + (int) Math.ceil((q - used) / 2.0);
        p.quantumHistory.add(p.quantum);
    }

    private void updateQuantumFull(Process p, int q, int used) {
        p.quantum = q + (q - used);
        p.quantumHistory.add(p.quantum);
    }

    public double getAverageWaitingTime() {
        return finished.stream().mapToInt(p -> p.waiting).average().orElse(0);
    }

    public double getAverageTurnaroundTime() {
        return finished.stream().mapToInt(p -> p.turnaround).average().orElse(0);
    }
}