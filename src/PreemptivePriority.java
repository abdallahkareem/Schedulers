import java.util.*;

public class PreemptivePriority implements Scheduler {
    private List<ProcessResult> processResults = new ArrayList<>();
    private List<Process> executionOrder = new LinkedList<>();
    private Map<Process, Integer> lastUpdateTime = new HashMap<>();
    private int agingInterval;

    public PreemptivePriority(int agingInterval) {
        this.agingInterval = agingInterval;
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {
        int numProcess = processes.size();
        int timer = 0;
        int finishCount = 0;

        // Reset process states and initialize 'last' to arrival time
        for (Process p : processes) {
            p.remaining = p.burst;
            lastUpdateTime.put(p, p.arrival);
        }

        Process currentlyOnCPU = null;

        while (finishCount < numProcess) {
            // 1. Check aging before selection
            applyAging(processes, timer);

            // 2. Choose the best process
            Process best = selectBestProcess(processes, timer);

            if (best == null) {
                timer++;
                continue;
            }

            // 3. Check for Context Switch (if process changes)
            if (best != currentlyOnCPU) {
                // If a process was actually running and is being switched out
                if (currentlyOnCPU != null && currentlyOnCPU.remaining > 0) {
                    lastUpdateTime.put(currentlyOnCPU, timer);
                }

                // Increase time by context switch
                timer += contextSwitch;

                // Re-check aging and re-select (recursive check)
                while (true) {
                    applyAging(processes, timer);
                    Process newBest = selectBestProcess(processes, timer);
                    if (newBest == best) break;
                    best = newBest;
                }

                // Dispatch selected process
                currentlyOnCPU = best;
            }

            // 4. Record execution order
            if (executionOrder.isEmpty() || executionOrder.get(executionOrder.size() - 1) != currentlyOnCPU) {
                executionOrder.add(currentlyOnCPU);
            }

            // 5. Execute for 1 time unit
            timer++;
            currentlyOnCPU.remaining--;

            // 6. Check for completion
            if (currentlyOnCPU.remaining == 0) {
                finishCount++;
                currentlyOnCPU.turnaround = timer - currentlyOnCPU.arrival;
                currentlyOnCPU.waiting = currentlyOnCPU.turnaround - currentlyOnCPU.burst;

                processResults.add(new ProcessResult(
                        currentlyOnCPU.name,
                        currentlyOnCPU.waiting,
                        currentlyOnCPU.turnaround,
                        new ArrayList<>()
                ));

                // CPU becomes free; next selection will trigger a CS if needed
                currentlyOnCPU = null;
            }
        }

        // Ensure results are sorted by name or arrival if required by your printer
        processResults.sort(Comparator.comparing(r -> r.name));
    }

    private void applyAging(List<Process> processes, int currentTime) {
        for (Process p : processes) {
            if (p.remaining > 0 && p.arrival <= currentTime) {
                int last = lastUpdateTime.get(p);
                // Hint: If ((Time - last) % agingInterval == 0)
                if (currentTime > last && (currentTime - last) % agingInterval == 0) {
                    if (p.priority > 1) {
                        p.priority--;
                        // Hint: Update last value when aging is applied
                        lastUpdateTime.put(p, currentTime);
                    }
                }
            }
        }
    }

    private Process selectBestProcess(List<Process> processes, int currentTime) {
        Process best = null;
        for (Process p : processes) {
            if (p.remaining > 0 && p.arrival <= currentTime) {
                if (best == null) {
                    best = p;
                } else {
                    // Primary: Priority (Lower is better)
                    if (p.priority < best.priority) {
                        best = p;
                    }
                    // Tie-breaker: Arrival order (Lower is better)
                    else if (p.priority == best.priority) {
                        if (p.arrival < best.arrival) {
                            best = p;
                        }
                    }
                }
            }
        }
        return best;
    }

    public List<Process> getExecutionOrder() { return executionOrder; }
    public List<ProcessResult> getProcessResults() { return processResults; }
}