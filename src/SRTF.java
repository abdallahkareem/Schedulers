import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

public class SRTF implements Scheduler {
    protected List<ProcessResult> processResults = new LinkedList<>();
    private List<Process> executionOrder = new LinkedList<>();

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    public List<ProcessResult> getProcessResults() {
        return processResults;
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {

        int numProcess = processes.size();
        int timer = 0;
        int finish = 0;

        // initialize remaining time
        for (Process p : processes) {
            p.remaining = p.burst;
        }

        List<Process> readyList = new LinkedList<>();
        Process prevProcess = null;

        while (finish < numProcess) {

            // add arrived processes to ready list
            for (Process p : processes) {
                if (p.remaining > 0 &&
                    p.arrival <= timer &&
                    !readyList.contains(p)) {
                    readyList.add(p);
                }
            }

            // select process with shortest remaining time
            Process current = null;
            int min = Integer.MAX_VALUE;

            for (Process p : readyList) {
                if (p.remaining < min) {
                    min = p.remaining;
                    current = p;
                }
            }

            // CPU idle
            if (current == null) {
                timer++;
                prevProcess = null;
                continue;
            }

            // context switch
            if (prevProcess != null && prevProcess != current) {
                timer += contextSwitch;
            }

            // record execution order (only on change)
            if (executionOrder.isEmpty() ||
                executionOrder.get(executionOrder.size() - 1) != current) {
                executionOrder.add(current);
            }

            // execute for 1 time unit
            timer++;
            current.remaining--;
            prevProcess = current;

            // process finished
            if (current.remaining == 0) {
                finish++;
                current.turnaround = timer - current.arrival;
                current.waiting = current.turnaround - current.burst;
                processResults.add(new ProcessResult(current.name, current.waiting, current.turnaround, new LinkedList<>()));
                readyList.remove(current);
            }
        }
    }



    public double getAverageWaitingTime() {
        int totalWaitingTime = 0;
        for (ProcessResult proc : processResults) {
            totalWaitingTime += proc.waitingTime;
        }
        return (double) totalWaitingTime / processResults.size();
    }



    public double getAverageTurnaroundTime() {
        int totalTurnaroundTime = 0;
        for (ProcessResult proc : processResults) {
            totalTurnaroundTime += proc.turnaroundTime;
        }
        return (double) totalTurnaroundTime / processResults.size();
    }
}