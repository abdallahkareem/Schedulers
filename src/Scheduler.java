import java.util.List;

public interface Scheduler {
	void run(List<Process> processes,int contextSwitch);
}
