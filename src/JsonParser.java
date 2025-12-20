import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static TestCaseInput parseTestCase(String fileName) {

        List<Process> processes = new ArrayList<>();

        try {
            String json = Files.readString(Paths.get(fileName));

            // -------- AG detection --------
            boolean isAG = json.contains("\"quantum\"");

            int contextSwitch = 0;
            int rrQuantum = 0;
            int agingInterval = 0;

            if (!isAG) {
                contextSwitch = extractInt(json, "contextSwitch");
                rrQuantum = extractInt(json, "rrQuantum");
                agingInterval = extractInt(json, "agingInterval");
            }

            // -------- Extract processes block (inside "input") --------
            String processBlock = json.substring(
                    json.indexOf("\"processes\""),
                    json.indexOf("]", json.indexOf("\"processes\"")) + 1
            );

            String[] entries = processBlock.split("\\{");

            for (String e : entries) {
                if (!e.contains("\"name\"")) continue;

                String name = extractString(e, "name");
                int arrival = extractInt(e, "arrival");
                int burst = extractInt(e, "burst");
                int priority = extractInt(e, "priority");

                int quantum;
                if (isAG) {
                    quantum = extractInt(e, "quantum"); // AG uses per-process quantum
                } else {
                    quantum = rrQuantum;
                }

                Process p = new Process(name, arrival, burst, priority, quantum);
                p.remaining = burst;

                processes.add(p);
            }

            return new TestCaseInput(
                    contextSwitch,
                    rrQuantum,
                    agingInterval,
                    processes
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ===== Helpers =====
    private static int extractInt(String text, String key) {
        String value = text.split("\"" + key + "\"\\s*:\\s*")[1]
                .split("[,}]")[0];
        return Integer.parseInt(value.trim());
    }

    private static String extractString(String text, String key) {
        return text.split("\"" + key + "\"\\s*:\\s*\"")[1]
                .split("\"")[0];
    }
}
