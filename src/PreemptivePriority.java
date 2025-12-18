import java.util.LinkedList;
import java.util.List;

public class PreemptivePriority implements Scheduler {

    private List<Process> executionOrder = new LinkedList<>();

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    public double getAverageWaitingTime() {
        double totalWaitingTime = 0;
        for (Process proc : executionOrder) {
            totalWaitingTime += proc.waiting;
        }
        return executionOrder.isEmpty() ? 0 : totalWaitingTime / executionOrder.size();
    }

    public double getAverageTurnaroundTime() {
        double totalTurnaroundTime = 0;
        for (Process proc : executionOrder) {
            totalTurnaroundTime += proc.turnaround;
        }
        return executionOrder.isEmpty() ? 0 : totalTurnaroundTime / executionOrder.size();
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {

        int n = processes.size();
        int timer = 0;
        int finished = 0;

        // initialize remaining burst
        for (Process p : processes) {
            p.remaining = p.burst;
        }

        List<Process> readyList = new LinkedList<>();
        Process prevProcess = null;

        while (finished < n) {

            // add arrived processes
            for (Process p : processes) {
                if (p.arrival <= timer && p.remaining > 0 && !readyList.contains(p)) {
                    readyList.add(p);
                }
            }

            // select highest priority process
            Process current = null;
            for (Process p : readyList) {
                if (current == null || p.priority < current.priority) {
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

            // execute for 1 unit (preemptive)
            executionOrder.add(current);
            current.remaining--;
            timer++;
            prevProcess = current;

            // finished
            if (current.remaining == 0) {
                finished++;
                current.turnaround = timer - current.arrival;
                current.waiting = current.turnaround - current.burst;
                readyList.remove(current);
            }
        }
    }
}
