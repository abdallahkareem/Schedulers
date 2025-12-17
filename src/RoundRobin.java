import java.util.LinkedList;
import java.util.List;

public class RoundRobin implements Scheduler {
    private List<Process> executionOrder = new LinkedList<>();

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }

    @Override
    public void run(List<Process> processes, int contextSwitch) {
        int NumProcess = processes.size();
        int timer = 0;
        int finish = 0;
        int timeQuantum = 20; // example quantum

        // initialize remaining burst
        for (Process proc : processes) {
            proc.remaining = proc.burst;
        }

        List<Process> readyList = new LinkedList<>();
        Process prevProcess = null;

        while (finish < NumProcess) {
            // add arrived processes to readyList
            for (Process proc : processes) {
                if (proc.remaining > 0 && proc.arrival <= timer && !readyList.contains(proc)) {
                    readyList.add(proc);
                }
            }

            Process current = null;
            if (!readyList.isEmpty()) {
                current = readyList.get(0); // pick the first process in readyList
            }

            if (current == null) { 
                timer++;
                prevProcess = null;
                continue;
            }

            if (prevProcess != null && prevProcess != current) {
                timer += contextSwitch;
            }

            // execute for up to timeQuantum
            int execTime = Math.min(timeQuantum, current.remaining);
            current.remaining -= execTime;
            timer += execTime;
            executionOrder.add(current);
            prevProcess = current;

            if (current.remaining == 0) { 
                finish++;
                current.turnaround = timer - current.arrival;
                current.waiting = current.turnaround - current.burst;
                readyList.remove(current);
            } else {
                readyList.remove(current);
                readyList.add(current);
            }
        }
    }
}
