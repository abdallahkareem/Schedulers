import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List<Process> processes = new ArrayList<>();
		Process process1 = new Process("p1", 0, 53, 1, 7);
		Process process2 = new Process("p2", 0, 17, 1, 9);
		Process process3 = new Process("p3", 0, 68, 1, 4);
		Process process4 = new Process("p4", 0, 24, 1, 4);
		processes.add(process1);
		processes.add(process2);
		processes.add(process3);
		processes.add(process4);
		RoundRobin scheduler = new RoundRobin();
		scheduler.run(processes ,0);
		SchedulerPrinter.print(scheduler.getExecutionOrder(), processes);
	}
}
