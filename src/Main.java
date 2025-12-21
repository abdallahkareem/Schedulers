public class Main {

    public static void main(String[] args) {

        // TestCaseInput input =
        //         JsonParser.parseTestCase("d:/Schedulers/Other_Schedulers/test_1.json");
        
        TestCaseInput input =
                JsonParser.parseTestCase("d:/Schedulers/AG/AG_test2.json");

        AGScheduler scheduler = new AGScheduler();

        scheduler.simulate(input.processes);

        AGPrinter.print(
                scheduler.getExecutionOrder(),
                input.processes
        );
    }
}
