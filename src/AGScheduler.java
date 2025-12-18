import java.util.*;

public class AGScheduler implements Scheduler {

    private List<Process> executionOrder = new LinkedList<>();
    private List<ProcessResult> processResults = new ArrayList<>();

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    public List<ProcessResult> getProcessResults() {
        return processResults;
    }

    public double getAverageWaitingTime() {
        double sum = 0;
        for (ProcessResult r : processResults)
            sum += r.waitingTime;
        return processResults.isEmpty() ? 0 : sum / processResults.size();
    }

    public double getAverageTurnaroundTime() {
        double sum = 0;
        for (ProcessResult r : processResults)
            sum += r.turnaroundTime;
        return processResults.isEmpty() ? 0 : sum / processResults.size();
    }

    // ======================= CORE =======================

    @Override
    public void run(List<Process> processes, int contextSwitch) {

        int time = 0;
        int completed = 0;
        int n = processes.size();

        for (Process p : processes)
            p.remaining = p.burst;

        Queue<Process> ready = new LinkedList<>();
        while (completed < n) {

            // add arrived processes
            for (Process p : processes) {
                if (p.arrival <= time && p.remaining > 0 && !ready.contains(p)) {
                    ready.add(p);
                }
            }

            if (ready.isEmpty()) {
                time++;
                continue;
            }

            Process current = ready.poll();
            recordExecution(current);

            int q = current.quantum;

            // ---------- 25% FCFS ----------
            int q1 = ceil(q * 0.25);
            time = execute(current, q1, time);
            if (finishIfDone(current, time)) {
                completed++;
                continue;
            }

            // ---------- 50% Priority ----------
            Process best = highestPriority(current, ready);
            if (best != current) {
                increaseQuantum(current, q - q1);
                ready.add(current);
                continue;
            }

            int q2 = ceil(q * 0.5) - q1;
            time = execute(current, q2, time);
            if (finishIfDone(current, time)) {
                completed++;
                continue;
            }

            // ---------- Remaining SJF ----------
            int remainingQ = q - (q1 + q2);
            while (remainingQ > 0) {

                Process shortest = shortestJob(current, ready);
                if (shortest != current) {
                    increaseQuantum(current, remainingQ);
                    ready.add(current);
                    break;
                }

                time = execute(current, 1, time);
                remainingQ--;

                if (finishIfDone(current, time)) {
                    completed++;
                    break;
                }
            }

            if (current.remaining > 0 && !ready.contains(current)) {
                increaseQuantum(current, 2);
                ready.add(current);
            }
        }
    }

    // ======================= HELPERS =======================

    private int execute(Process p, int units, int time) {
        for (int i = 0; i < units && p.remaining > 0; i++) {
            p.remaining--;
            time++;
        }
        recordExecution(p);
        return time;
    }

    private boolean finishIfDone(Process p, int time) {
        if (p.remaining == 0) {
            p.quantumHistory.add(0);

            int turnaround = time - p.arrival;
            int waiting = turnaround - p.burst;

            processResults.add(
                new ProcessResult(
                    p.name,
                    waiting,
                    turnaround,
                    new ArrayList<>(p.quantumHistory)
                )
            );

            p.quantum = 0;
            return true;
        }
        return false;
    }

    private void increaseQuantum(Process p, int inc) {
        p.quantum += ceil(inc / 2.0);
        p.quantumHistory.add(p.quantum);
    }

    private void recordExecution(Process p) {
        if (executionOrder.isEmpty() ||
            executionOrder.get(executionOrder.size() - 1) != p) {
            executionOrder.add(p);
        }
    }

    private Process highestPriority(Process current, Queue<Process> q) {
        Process best = current;
        for (Process p : q) {
            if (p.priority < best.priority)
                best = p;
        }
        return best;
    }

    private Process shortestJob(Process current, Queue<Process> q) {
        Process best = current;
        for (Process p : q) {
            if (p.remaining < best.remaining)
                best = p;
        }
        return best;
    }

    private int ceil(double x) {
        return (int) Math.ceil(x);
    }
}
