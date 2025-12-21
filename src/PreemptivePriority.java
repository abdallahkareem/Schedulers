import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
        int n = processes.size();

        Map<Process, Integer> lastAgingTimeTracker = new HashMap<>();
        for (Process p : processes) {
            p.remaining = p.burst;
            p.waiting = 0;
            p.turnaround = 0;
            lastAgingTimeTracker.put(p, p.arrival);
        }

        List<Process> readyList = new LinkedList<>();
        Process runningProcess = null;

        while (finished < n) {
            updateReadyList(processes, readyList, timer);

            if (readyList.isEmpty()) {
                timer++;
                continue;
            }

            applyAging(readyList, runningProcess, timer, lastAgingTimeTracker);
            Process highest = findHighestPriority(readyList);

            // --- CONTEXT SWITCH LOGIC ---
            // If the process we want to run is NOT the one currently on the CPU
            if (runningProcess != highest) {
                // Apply context switch if we were already running something
                // OR if this is the very first process and it didn't arrive at t=0
                if (runningProcess != null || timer > 0) {
                    for (int i = 0; i < contextSwitch; i++) {
                        timer++;
                        updateReadyList(processes, readyList, timer);
                    }
                    // Re-find highest in case someone better arrived during the switch
                    highest = findHighestPriority(readyList);
                }

                runningProcess = highest;
                if (executionOrder.isEmpty() || executionOrder.get(executionOrder.size() - 1) != runningProcess) {
                    executionOrder.add(runningProcess);
                }
            }

            // Execute for 1 time unit
            runningProcess.remaining--;
            timer++;

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
                // Reset runningProcess to null so the NEXT loop triggers a context switch
                runningProcess = null;
            }
        }
    }

    private void updateReadyList(List<Process> allProcesses, List<Process> readyList, int currentTime) {
        for (Process p : allProcesses) {
            if (p.arrival <= currentTime && p.remaining > 0 && !readyList.contains(p)) {
                readyList.add(p);
            }
        }
    }

    private void applyAging(List<Process> readyList, Process running, int currentTime, Map<Process, Integer> tracker) {
        for (Process p : readyList) {
            if (p != running) {
                int lastAged = tracker.get(p);
                // If interval reached, improve priority (lower the number)
                if (currentTime - lastAged >= agingInterval && agingInterval > 0) {
                    p.priority = Math.max(0, p.priority - 1);
                    tracker.put(p, currentTime);
                }
            }
        }
    }

    private Process findHighestPriority(List<Process> readyList) {
        Process highest = readyList.get(0);
        for (Process p : readyList) {
            // Priority tie-break: earlier arrival time
            if (p.priority < highest.priority ||
                    (p.priority == highest.priority && p.arrival < highest.arrival)) {
                highest = p;
            }
        }
        return highest;
    }

    public double getAverageWaitingTime() {
        if (processResults.isEmpty()) return 0;
        double total = 0;
        for (ProcessResult p : processResults) total += p.waitingTime;
        return total / processResults.size();
    }

    public double getAverageTurnaroundTime() {
        if (processResults.isEmpty()) return 0;
        double total = 0;
        for (ProcessResult p : processResults) total += p.turnaroundTime;
        return total / processResults.size();
    }
}