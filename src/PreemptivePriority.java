import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PreemptivePriority implements Scheduler {

    protected List<ProcessResult> processResults = new ArrayList<>();
    private List<Process> executionOrder = new LinkedList<>();
    private int agingInterval;

    public PreemptivePriority(int agingInterval) {
        this.agingInterval = agingInterval;
    }

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    public List<ProcessResult> getProcessResults() {
        return processResults;
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {
        int timer = 0;
        int finished = 0;

        for (Process p : processes) {
            p.remaining = p.burst;
            p.lastAgingTime = p.arrival;
        }

        List<Process> readyList = new LinkedList<>();
        Process runningProcess = null;

        while (finished < processes.size()) {
            // Add newly arrived processes to ready list
            for (Process p : processes) {
                if (p.remaining > 0 && p.arrival <= timer && !readyList.contains(p)) {
                    readyList.add(p);
                }
            }

            if (readyList.isEmpty()) {
                timer++;
                runningProcess = null;
                continue;
            }

            // Aging: increase priority for waiting processes
            for (Process p : readyList) {
                if (timer - p.lastAgingTime >= agingInterval) {
                    p.priority = Math.max(0, p.priority - 1); // lower number = higher priority
                    p.lastAgingTime = timer;
                }
            }

            // Select highest priority process
            Process highest = readyList.get(0);
            for (Process p : readyList) {
                if (p.priority < highest.priority ||
                   (p.priority == highest.priority && p.arrival < highest.arrival)) {
                    highest = p;
                }
            }

            // Preempt only if new process has higher priority
            if (runningProcess != highest) {
                if (runningProcess != null) {
                    timer += contextSwitch; // add context switch time
                }
                runningProcess = highest;
                executionOrder.add(runningProcess);
            }

            // Execute 1 unit
            runningProcess.remaining--;
            timer++;

            // Check if process finished
            if (runningProcess.remaining == 0) {
                finished++;
                runningProcess.turnaround = timer - runningProcess.arrival;
                runningProcess.waiting = runningProcess.turnaround - runningProcess.burst;

                processResults.add(new ProcessResult(
                        runningProcess.name,
                        runningProcess.waiting,
                        runningProcess.turnaround,
                        new ArrayList<>()
                ));

                readyList.remove(runningProcess);
                runningProcess = null; // force next iteration to pick new process
            }
        }
    }

    public double getAverageWaitingTime() {
        double total = 0;
        for (ProcessResult p : processResults) total += p.waitingTime;
        return total / processResults.size();
    }

    public double getAverageTurnaroundTime() {
        double total = 0;
        for (ProcessResult p : processResults) total += p.turnaroundTime;
        return total / processResults.size();
    }
}
