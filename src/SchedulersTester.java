import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchedulersTester {

    private String jsonFilePath;

    public SchedulersTester(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public void runTests() throws Exception {
        JsonParser parser = new JsonParser(jsonFilePath);
        TestCase testCase = parser.parse();

        System.out.println("Running test: " + testCase.name + "\n");

        List<Process> originalProcesses = deepCopyProcesses(testCase.input.processes);

        // Determine format: old vs new
        if (testCase.expectedOutput instanceof Map) {
            // Old format: multiple algorithms
            Map<String, AlgorithmResult> expectedMap = (Map<String, AlgorithmResult>) testCase.expectedOutput;

            // SJF
            if (expectedMap.containsKey("SJF")) {
                SRTF sjf = new SRTF();
                sjf.run(deepCopyProcesses(originalProcesses), testCase.input.contextSwitch);
                compareResults("SJF", sjf.getProcessResults(), expectedMap.get("SJF"));
            }

            // RR
            if (expectedMap.containsKey("RR")) {
                RoundRobin rr = new RoundRobin(testCase.input.rrQuantum);
                rr.run(deepCopyProcesses(originalProcesses), testCase.input.contextSwitch);
                compareResults("RR", rr.getProcessResults(), expectedMap.get("RR"));
            }

            // Priority
            if (expectedMap.containsKey("Priority")) {
                PreemptivePriority prio = new PreemptivePriority();
                prio.run(deepCopyProcesses(originalProcesses), testCase.input.contextSwitch);
                compareResults("Priority", prio.getProcessResults(), expectedMap.get("Priority"));
            }
        } else if (testCase.expectedOutput instanceof AlgorithmResult) {
            // New format: single algorithm
            AlgorithmResult expected = (AlgorithmResult) testCase.expectedOutput;

            // Determine which algorithm to run based on JSON content
            if (expected.executionOrder != null && !expected.executionOrder.isEmpty()) {
                // Try RR if quantum is defined
                if (testCase.input.rrQuantum > 0) {
                    RoundRobin rr = new RoundRobin(testCase.input.rrQuantum);
                    rr.run(deepCopyProcesses(originalProcesses), testCase.input.contextSwitch);
                    compareResults("Round Robin", rr.getProcessResults(), expected);
                } 
                // Or AG Scheduler if agingInterval defined
                else if (testCase.input.agingInterval > 0) {
                    AGScheduler ag = new AGScheduler();
                    ag.run(deepCopyProcesses(originalProcesses), testCase.input.contextSwitch);
                    compareResults("AG Scheduler", ag.getProcessResults(), expected);
                } 
                // Otherwise, default to SRTF
                else {
                    SRTF sjf = new SRTF();
                    sjf.run(deepCopyProcesses(originalProcesses), testCase.input.contextSwitch);
                    compareResults("SRTF", sjf.getProcessResults(), expected);
                }
            }
        }
    }

    private List<Process> deepCopyProcesses(List<Process> processes) {
        List<Process> copy = new ArrayList<>();
        for (Process p : processes) {
            copy.add(new Process(p.name, p.arrival, p.burst, p.priority, p.quantum != null ? p.quantum : 0));
        }
        return copy;
    }

    private void compareResults(String algoName, List<ProcessResult> actualResults, AlgorithmResult expected) {
        System.out.println("Algorithm: " + algoName);

        boolean success = true;

        // Execution order
        List<String> actualOrder = new ArrayList<>();
        for (ProcessResult pr : actualResults) actualOrder.add(pr.name);

        if (expected.executionOrder != null && !expected.executionOrder.equals(actualOrder)) {
            System.out.println("Execution order mismatch!");
            System.out.println("Expected: " + expected.executionOrder);
            System.out.println("Actual:   " + actualOrder);
            success = false;
        }

        // Process results
        for (ProcessResult expPr : expected.processResults) {
            boolean found = false;
            for (ProcessResult actPr : actualResults) {
                if (actPr.name.equals(expPr.name)) {
                    found = true;
                    if (actPr.waitingTime != expPr.waitingTime || actPr.turnaroundTime != expPr.turnaroundTime) {
                        System.out.println("Mismatch in process: " + actPr.name);
                        System.out.println("Expected waiting/turnaround: " + expPr.waitingTime + "/" + expPr.turnaroundTime);
                        System.out.println("Actual waiting/turnaround:   " + actPr.waitingTime + "/" + actPr.turnaroundTime);
                        success = false;
                    }
                    break;
                }
            }
            if (!found) {
                System.out.println("Process missing: " + expPr.name);
                success = false;
            }
        }

        // Average waiting/turnaround
        double avgWait = actualResults.stream().mapToDouble(r -> r.waitingTime).average().orElse(0);
        double avgTurn = actualResults.stream().mapToDouble(r -> r.turnaroundTime).average().orElse(0);
        if (Math.abs(avgWait - expected.averageWaitingTime) > 1e-6 ||
            Math.abs(avgTurn - expected.averageTurnaroundTime) > 1e-6) {
            System.out.println("Average waiting/turnaround mismatch!");
            System.out.println("Expected: " + expected.averageWaitingTime + "/" + expected.averageTurnaroundTime);
            System.out.println("Actual:   " + avgWait + "/" + avgTurn);
            success = false;
        }

        if (success) {
            System.out.println(algoName + " ✅ Passed!\n");
        } else {
            System.out.println(algoName + " ❌ Failed!\n");
        }
    }

    public static void main(String[] args) throws Exception {
        SchedulersTester tester = new SchedulersTester("testcase.json");
        tester.runTests();
    }
}
