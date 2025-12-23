import java.util.List;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Ryad\\IdeaProjects\\Schedulers\\Other_Schedulers\\test_5.json";
        TestCaseInput input = JsonParser.parseTestCase(filePath);

        if (input == null) {
            System.out.println("Failed to parse input file.");
            return;
        }

        boolean isAGTest = (input.rrQuantum == 0 && input.agingInterval == 0);

        if (isAGTest) {
            System.out.println("--- Running AG Scheduler ---");
            AGScheduler agScheduler = new AGScheduler();
            agScheduler.simulate(input.processes);
            AGPrinter.print(agScheduler.getExecutionOrder(), input.processes);
        } else {
            // --- 1. SJF (SRTF) Way ---
            System.out.println("--- SJF (SRTF) Results ---");
            List<Process> sjfProcesses = resetProcesses(input.processes);
            SRTF srtf = new SRTF();
            srtf.run(sjfProcesses, input.contextSwitch);
            SchedulerPrinter.print(srtf.getExecutionOrder(), sjfProcesses);

            // --- 2. Round Robin Way ---
            System.out.println("\n--- RR Results ---");
            List<Process> rrProcesses = resetProcesses(input.processes);
            RoundRobin rr = new RoundRobin(input.rrQuantum);
            rr.run(rrProcesses, input.contextSwitch);
            SchedulerPrinter.print(rr.getExecutionOrder(), rrProcesses);

            // --- 3. Priority Way ---
            System.out.println("\n--- Priority Results ---");
            List<Process> priorityProcesses = resetProcesses(input.processes);
            PreemptivePriority priority = new PreemptivePriority(input.agingInterval);
            priority.run(priorityProcesses, input.contextSwitch);
            SchedulerPrinter.print(priority.getExecutionOrder(), priorityProcesses);
        }
    }

    /**
     * Creates a deep copy of the process list to reset execution data
     * (remaining time, waiting, turnaround) for the next scheduler.
     */
    private static List<Process> resetProcesses(List<Process> original) {
        List<Process> newList = new ArrayList<>();
        for (Process p : original) {
            // Create a fresh process instance using original values
            Process resetP = new Process(p.name, p.arrival, p.burst, p.priority, p.quantum);
            resetP.remaining = p.burst;
            resetP.waiting = 0;
            resetP.turnaround = 0;
            newList.add(resetP);
        }
        return newList;
    }
}