import java.util.ArrayList;
import java.util.List;

class ProcessResult {
    String name;
    int waitingTime;
    int turnaroundTime;
    List<Integer> quantumHistory = new ArrayList<>();

    public ProcessResult(String name, int waitingTime, int turnaroundTime, List<Integer> quantumHistory) {
        this.name = name;
        this.waitingTime = waitingTime;
        this.turnaroundTime = turnaroundTime;
        this.quantumHistory = quantumHistory;
    }
}
