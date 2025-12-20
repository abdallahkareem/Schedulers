import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Process> processes = new ArrayList<>();

        processes.add(new Process("P1", 0, 10, 3, 4));
        processes.add(new Process("P2", 0, 8, 1, 5));
        processes.add(new Process("P3", 0, 12, 2, 6));
        processes.add(new Process("P4", 0, 6, 4, 3));
        processes.add(new Process("P5", 0, 9, 5, 4));

        AGScheduler scheduler = new AGScheduler();
        scheduler.run(processes, 0);

        SchedulerPrinter.print(
                scheduler.getExecutionOrder(),
                scheduler.getFinishedProcesses()
        );
    }
}
