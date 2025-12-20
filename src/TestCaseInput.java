import java.util.List;

public class TestCaseInput {
    public int contextSwitch;
    public int rrQuantum;
    public int agingInterval;
    public List<Process> processes;

    public TestCaseInput(int contextSwitch, int rrQuantum,
                         int agingInterval, List<Process> processes) {
        this.contextSwitch = contextSwitch;
        this.rrQuantum = rrQuantum;
        this.agingInterval = agingInterval;
        this.processes = processes;
    }
}
