import java.util.List;

public class AGPrinter {
        public static void print(List<Process> executionOrder, List<Process> processes) {

        // executionOrder
        System.out.print("\"executionOrder\": [");
        int totaltime = 0 ;
        int totalturnAround = 0;
        for (Process p : processes) {
             totaltime += p.waiting;
             totalturnAround += p.turnaround;
        }
        for (int i = 0; i < executionOrder.size(); i++) {
            System.out.print("\"" + executionOrder.get(i).name + "\"");
            if (i < executionOrder.size() - 1) System.out.print(", ");
        }
        System.out.println("],");

        // processResults
        System.out.println("\"processResults\": [");
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);

            System.out.print("  {\"name\": \"" + p.name + "\", ");
            System.out.print("\"waitingTime\": " + p.waiting + ", ");
            System.out.print("\"turnaroundTime\": " + p.turnaround + ", ");
            System.out.print("\"QuantumHistory\": " + p.quantumHistory + "}");

            if (i < processes.size() - 1) System.out.print(",");
            System.out.println();
        }
        System.out.println("],");
        System.out.println("Average Waiting Time = " + (double) totaltime / processes.size());
        System.out.println("Average Turnaround Time = " + (double) totalturnAround / processes.size());
    }
}
