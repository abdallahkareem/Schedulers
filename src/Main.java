import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Process> processes = new ArrayList<>();

        Process process1 = new Process("p1", 2, 6, 3, 0);
        Process process2 = new Process("p2", 5, 2, 1, 0);
        Process process3 = new Process("p3", 1, 8, 4, 0);
        Process process4 = new Process("p4", 0, 3, 5, 0);
        Process process5 = new Process("p5", 4, 4, 2, 0);

        processes.add(process1);
        processes.add(process2);
        processes.add(process3);
        processes.add(process4);
        processes.add(process5);

        PreemptivePriority scheduler = new PreemptivePriority();
        scheduler.run(processes, 0);

        SchedulerPrinter.print(
                scheduler.getExecutionOrder(),
                processes
        );
    }
}
