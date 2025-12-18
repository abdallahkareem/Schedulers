import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RoundRobin implements Scheduler {

    protected List<ProcessResult> processResults = new ArrayList<>();
    public List<ProcessResult> getProcessResults() {
        return processResults;
    }
    
    private List<Process> executionOrder = new LinkedList<>();
    private int timeQuantum;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {
        int numProcess = processes.size();
        int timer = 0;
        int finish = 0;

        // initialize remaining burst
        for (Process proc : processes) {
            proc.remaining = proc.burst;
        }

        List<Process> readyList = new LinkedList<>();
        Process prevProcess = null;

        while (finish < numProcess) {

            // add arrived processes
            for (Process proc : processes) {
                if (proc.remaining > 0 &&
                    proc.arrival <= timer &&
                    !readyList.contains(proc)) {
                    readyList.add(proc);
                }
            }

            if (readyList.isEmpty()) {
                timer++;
                prevProcess = null;
                continue;
            }

            Process current = readyList.remove(0);

            if (prevProcess != null && prevProcess != current) {
                timer += contextSwitch;
            }

            int execTime = Math.min(timeQuantum, current.remaining);
            current.remaining -= execTime;
            timer += execTime;

            executionOrder.add(current);
            prevProcess = current;

            if (current.remaining == 0) {
                finish++;
                current.turnaround = timer - current.arrival;
                current.waiting = current.turnaround - current.burst;
                processResults.add(new ProcessResult(current.name, current.waiting, current.turnaround, new ArrayList<>()));
            } else {
                readyList.add(current);
            }
        }
    }

    public double getAverageWaitingTime() {
        double totalWaitingTime = 0;
        for (ProcessResult proc : processResults) {
            totalWaitingTime += proc.waitingTime;
        }
        return totalWaitingTime / processResults.size();
    }

    public double getAverageTurnaroundTime() {
        double totalTurnaroundTime = 0;
        for (ProcessResult proc : processResults) {
            totalTurnaroundTime += proc.turnaroundTime;
        }
        return totalTurnaroundTime / processResults.size();
    }
}