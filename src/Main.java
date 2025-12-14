import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List processes = new ArrayList<>();
		Process process1 = new Process("p1", 0, 17, 4, 7);
		Process process2 = new Process("p2", 2, 6, 7, 9);
		Process process3 = new Process("p3", 5, 11, 3, 4);
		Process process4 = new Process("p4", 15, 4, 6, 6);
		processes.add(process1);
		processes.add(process2);
		processes.add(process3);
		processes.add(process4);
		AGScheduler scheduler = new AGScheduler();
		scheduler.run(processes);
	}

}
