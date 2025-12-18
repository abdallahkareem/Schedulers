import java.util.List;

public class SchedulersTester implements Tester<SchedularTestCase> {
    private SRTF sjf = new SRTF();
    private RoundRobin rr = new RoundRobin();
    private PreemptivePriority priority = new PreemptivePriority();
    @Override
    public void test(SchedularTestCase testCase) {
        
        sjf.run(testCase.processes, testCase.contextSwitch);
        rr.run(testCase.processes, testCase.contextSwitch);
        priority.run(testCase.processes, testCase.contextSwitch);
        // Validate SJF
        if (!checkExecutionOrder(sjf.getExecutionOrder(), testCase.expectedOutput.SJF.executionOrder) ||
                !checkAverageWaitingTime(sjf.getAverageWaitingTime(), testCase.expectedOutput.SJF.averageWaitingTime) ||
                !checkAverageTurnaroundTime(sjf.getAverageTurnaroundTime(), testCase.expectedOutput.SJF.averageTurnaroundTime) ||
                // Validate RR
            !checkExecutionOrder(rr.getExecutionOrder(), testCase.expectedOutput.RR.executionOrder) ||
                !checkAverageWaitingTime(rr.getAverageWaitingTime(), testCase.expectedOutput.RR.averageWaitingTime) ||
                !checkAverageTurnaroundTime(rr.getAverageTurnaroundTime(), testCase.expectedOutput.RR.averageTurnaroundTime) ||
                // Validate Priority
            !checkExecutionOrder(priority.getExecutionOrder(), testCase.expectedOutput.Priority.executionOrder) ||
                !checkAverageWaitingTime(priority.getAverageWaitingTime(), testCase.expectedOutput.Priority.averageWaitingTime) ||
                !checkAverageTurnaroundTime(priority.getAverageTurnaroundTime(), testCase.expectedOutput.Priority.averageTurnaroundTime)) {
            System.out.println("testCase: " + testCase.testname + " Failed");
            return;
        }


    }

    private boolean checkExecutionOrder(List<Process> expected, List<String> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (expected.get(i).name != actual.get(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAverageWaitingTime(double expected, double actual) {
        return expected == actual;
    }

    private boolean checkAverageTurnaroundTime(double expected, double actual) {
        return expected == actual;
    }

    public boolean checkProcessResults(List<ProcessResult> expected, List<ProcessResult> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            ProcessResult expectedResult = expected.get(i);
            ProcessResult actualResult = actual.get(i);
            if (!expectedResult.processName.equals(actualResult.processName) ||
                    expectedResult.waitingTime != actualResult.waitingTime ||
                    expectedResult.turnaroundTime != actualResult.turnaroundTime) {
                return false;
            }
        }
        return true;
    }
}
