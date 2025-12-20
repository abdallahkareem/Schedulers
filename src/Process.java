import java.util.ArrayList;
import java.util.List;

public class Process {

    public String name;
    public int arrival;
    public int burst;
    public int priority;
    public int quantum;

    public int remaining;
    public int completionTime = -1;
    public int waiting;
    public int turnaround;

    public List<Integer> quantumHistory = new ArrayList<>();

    public Process(String name, int arrival, int burst, int priority, int quantum) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.quantum = quantum;

        this.remaining = burst;
        this.quantumHistory.add(quantum); // initial quantum
    }
}
