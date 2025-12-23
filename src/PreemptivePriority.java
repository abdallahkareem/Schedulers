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

        // Initialize 'last' time for all processes to their arrival time
        for (Process p : processes) {
            p.remaining = p.burst;
            lastUpdateTime.put(p, p.arrival);
        }

        // 'lastCpuOwner' tracks the last process to execute, even if it finished.
        // This is used to detect if a context switch is needed (loading new process).
        Process lastCpuOwner = null;

        // 'currentlyRunning' tracks the process actively consuming the CPU *in this tick*.
        Process currentlyRunning = null;

        while (finishCount < numProcess) {

            // 1. Apply Aging (including to the running process)
            applyAging(processes, timer);

            // 2. Select the best process
            Process best = selectBestProcess(processes, timer);

            // 3. Handle Context Switching
            // A switch is needed if the chosen 'best' is different from the last one who held the CPU.
            // Exception: No switch penalty at T=0 (lastCpuOwner is null).
            if (best != null && lastCpuOwner != null && best != lastCpuOwner) {

                // If the previous owner is still alive (preempted), update its 'last' time
                // We update this BEFORE the switch time passes.
                if (currentlyRunning != null && currentlyRunning.remaining > 0) {
                    lastUpdateTime.put(currentlyRunning, timer);
                }

                // Perform Context Switch (Pass time)
                // During these ticks, the CPU is technically "switching", so no process executes.
                // However, aging MUST continue.
                for (int i = 0; i < contextSwitch; i++) {
                    timer++;
                    applyAging(processes, timer);
                }

                // After switch duration, set the new owner.
                // We do NOT execute yet. We loop back to re-evaluate.
                // Why? Because during the switch, someone else might have arrived or aged.
                lastCpuOwner = best;
                currentlyRunning = null; // No one ran this tick, we just switched.
                continue;
            }

            // 4. Case: CPU Idle (No best process found)
            if (best == null) {
                timer++;
                currentlyRunning = null;
                continue;
            }

            // 5. Case: Ready to Execute (best == lastCpuOwner OR first process)
            // If this is the very first process (lastCpuOwner == null), set it now.
            if (lastCpuOwner == null) {
                lastCpuOwner = best;
            }

            currentlyRunning = best;

            // Record execution order
            if (executionOrder.isEmpty() || executionOrder.get(executionOrder.size() - 1) != currentlyRunning) {
                executionOrder.add(currentlyRunning);
            }

            // Execute Tick
            timer++;
            currentlyRunning.remaining--;

            // 6. Check Completion
            if (currentlyRunning.remaining == 0) {
                finishCount++;
                currentlyRunning.turnaround = timer - currentlyRunning.arrival;
                currentlyRunning.waiting = currentlyRunning.turnaround - currentlyRunning.burst;

                processResults.add(new ProcessResult(
                        currentlyRunning.name,
                        currentlyRunning.waiting,
                        currentlyRunning.turnaround,
                        new ArrayList<>()
                ));

                // Process finished. It is no longer "running", but it remains 'lastCpuOwner'
                // so that if a NEW process starts next, we trigger a context switch.
                currentlyRunning = null;
            }
        }

        processResults.sort(Comparator.comparing(r -> r.name));
    }

    private void applyAging(List<Process> processes, int currentTime) {
        for (Process p : processes) {
            // Apply aging to ALL arrived, incomplete processes (Ready AND Running)
            if (p.remaining > 0 && p.arrival <= currentTime) {
                int last = lastUpdateTime.get(p);

                // Formula: If ((Time - last) % agingInterval == 0)
                if (currentTime > last && (currentTime - last) % agingInterval == 0) {
                    if (p.priority > 1) {
                        p.priority--;
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
                    // 1. Priority (Lower is better)
                    if (p.priority < best.priority) {
                        best = p;
                    }
                    // 2. Tie-breaker: Arrival Time (Lower is better)
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