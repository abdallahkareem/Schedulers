
import java.util.*;

public class AGScheduler {


    private List<Process> executionOrder = new ArrayList<>();
    private List<Process> finished = new ArrayList<>();
    public List<Process> getExecutionOrder() {
        return executionOrder;
    }
    private int currentTime = 0;

    public List<Process> getFinishedProcesses() {
        return finished;
    }

    public void simulate(List<Process> Processes) {

        List<Process> notArrived = new ArrayList<>(Processes);
        notArrived.sort(Comparator.comparingInt(p -> p.arrival));

        Queue<Process> readyQueue = new LinkedList<>();
        Process current = null;
        while (true) {

            boolean allDone = true;
            for (Process p : Processes) {
                if (p.remaining > 0) {
                    allDone = false;
                    break;
                }
            }
            if (allDone)
                break;

            addArrivals(notArrived, readyQueue , currentTime);

            if (current == null) {
                if (readyQueue.isEmpty()) {
                    currentTime++;
                    continue;
                }
                current = readyQueue.poll();
                executionOrder.add(current);
            }

            current = executeWithPhases(current, readyQueue, notArrived);
        }
    }

    private Process executeWithPhases(Process process, Queue<Process> readyQueue,List<Process> notArrived) {
        

        int originalQuantum = process.quantum;
        int usedTime = 0;

        /* ================= Phase 1 : FCFS (25%) ================= */
        int phase1 = (int) Math.ceil(originalQuantum * 0.25);

        for (int i = 0; i < phase1 && process.remaining> 0; i++) {
            currentTime++;
            usedTime++;
            process.remaining--;
            addArrivals(notArrived, readyQueue , currentTime);
        }

        if (process.remaining == 0) {
            process.completionTime = currentTime;
            process.turnaround = currentTime - process.arrival;
            process.waiting = process.turnaround - process.burst;
            process.quantumHistory.add(0);
            return null;
        }

        Process higher = existsHigherPriority(process , readyQueue);
        if (higher != null) {

            int newQ = originalQuantum + (int) Math.ceil((originalQuantum - usedTime) / 2.0);

            process.quantum = newQ;
            process.quantumHistory.add(newQ);

            readyQueue.add(process);
            readyQueue.remove(higher);
            executionOrder.add(higher);
            return higher;
        }

        /* ================= Phase 2 : Priority (25%) ================= */
        int phase2 = (int) Math.ceil(originalQuantum * 0.25);

        for (int i = 0; i < phase2 && process.remaining > 0; i++) {
            currentTime++;
            usedTime++;
            process.remaining--;
            addArrivals(notArrived, readyQueue , currentTime);
        }

        if (process.remaining == 0) {
            process.completionTime = currentTime;
            process.turnaround = currentTime - process.arrival;
            process.waiting = process.turnaround - process.burst;
            process.quantumHistory.add(0);
            return null;
        }

        Process shorter = existsShorter(process , readyQueue);
        if (shorter != null) {
            int newQ = originalQuantum + (originalQuantum - usedTime);

            process.quantum = newQ;
            process.quantumHistory.add(newQ);

            readyQueue.add(process);
            readyQueue.remove(shorter);
            executionOrder.add(shorter);
            return shorter;
        }

        /* ================= Phase 3 : Preemptive SJF ================= */
        int phase3 = originalQuantum - usedTime;

        for (int i = 0; i < phase3 && process.remaining > 0; i++) {
            currentTime++;
            usedTime++;
            process.remaining--;
            addArrivals(notArrived, readyQueue , currentTime);

            Process sjf = existsShorter(process, readyQueue);
            if (sjf != null) {
                int newQ = originalQuantum + (originalQuantum - usedTime);

                process.quantum = newQ;
                process.quantumHistory.add(newQ);

                readyQueue.add(process);
                readyQueue.remove(sjf);
                executionOrder.add(sjf);
                return sjf;
            }
        }

        if (process.remaining == 0) {
            process.completionTime = currentTime;
            process.turnaround = currentTime - process.arrival;
            process.waiting = process.turnaround - process.burst;
            process.quantumHistory.add(0);
            return null;
        }

        /* ================= Scenario (i): Quantum exhausted ================= */
        int newQ = originalQuantum + 2;
        process.quantum = newQ;
        process.quantumHistory.add(newQ);
        readyQueue.add(process);

        return null;
    }

    private void addArrivals(List<Process> notArrived, Queue<Process> ready , int time) {
        while (!notArrived.isEmpty() && notArrived.get(0).arrival <= time){
            ready.add(notArrived.remove(0));
        }
    }

    private Process existsHigherPriority(Process current, Queue<Process> ready) {
        Process higher = null;
        for (Process p : ready)
            if (p.priority < current.priority)
                if (higher == null || p.priority < higher.priority) {
                    higher = p;
                }
        return higher;
    }

    private Process existsShorter(Process current, Queue<Process> ready) {
        Process shorter = null;
        for (Process p : ready)
            if (p.remaining < current.remaining)
                if (shorter == null || p.remaining < shorter.remaining) {
                    shorter = p;
                }
        return shorter;
    }

}
