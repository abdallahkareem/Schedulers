public class Main {

    public static void main(String[] args) {

        TestCaseInput input =
                JsonParser.parseTestCase("Other_Schedulers/test_1.json");

        SRTF scheduler = new SRTF();

        scheduler.run(input.processes, input.contextSwitch);

        SchedulerPrinter.print(
                scheduler.getExecutionOrder(),
                input.processes
        );
    }
}
