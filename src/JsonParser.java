import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    private String json;

    


    public JsonParser(String filePath) throws Exception {
        json = Files.readString(Path.of(filePath));
        json = json.replaceAll("\\s+", ""); // remove whitespace
    }

    public TestCase parse() {
        TestCase tc = new TestCase();
        tc.name = extractString("name");

        tc.input = parseInput();
        tc.expectedOutput =  (Object) parseExpectedOutput();

        return tc;
    }

    // ================== INPUT ==================

    private InputData parseInput() {
        InputData input = new InputData();
        String inputBlock = extractBlock("input");

        input.contextSwitch = extractInt(inputBlock, "contextSwitch");
        input.rrQuantum = extractInt(inputBlock, "rrQuantum");
        input.agingInterval = extractInt(inputBlock, "agingInterval");

        String processesBlock = extractArray(inputBlock, "processes");
        String[] processObjects = splitObjects(processesBlock);
        for (String p : processObjects) {
            if (p.trim().isEmpty()) continue; // skip empty entries
            Process proc = new Process(p, 0, 0, 0, 0);
            proc.name = extractString(p, "name");
            proc.arrival = extractInt(p, "arrival");
            proc.burst = extractInt(p, "burst");
            proc.priority = extractInt(p, "priority");
            input.processes.add(proc);
        }

        return input;
    }

    // ================== EXPECTED OUTPUT ==================

    private Object parseExpectedOutput() {

    String out = extractBlock("expectedOutput");

    // CASE 1: Algorithm-based (old format)
    if (out.contains("\"SJF\"") || out.contains("\"RR\"")) {
        return parseExpectedOutput(); // old logic
    }

    // CASE 2: Single algorithm (new format)
    AlgorithmResult ar = new AlgorithmResult();

    ar.executionOrder = extractStringArray(out, "executionOrder");

    String prBlock = extractArray(out, "processResults");
    for (String pr : splitObjects(prBlock)) {
        ProcessResult r = new ProcessResult(pr, 0, 0, null);
        r.name = extractString(pr, "name");
        r.waitingTime = extractInt(pr, "waitingTime");
        r.turnaroundTime = extractInt(pr, "turnaroundTime");

        if (hasKey(pr, "quantumHistory")) {
            r.quantumHistory =
                extractIntArray(pr, "quantumHistory");
        }

        ar.processResults.add(r);
    }

    ar.averageWaitingTime =
        extractDouble(out, "averageWaitingTime");
    ar.averageTurnaroundTime =
        extractDouble(out, "averageTurnaroundTime");

    return ar;
}


    // ================== HELPERS ==================

    private String extractBlock(String key) {
        int start = json.indexOf("\"" + key + "\":{") + key.length() + 3;
        return extractCurlyBlock(json, start);
    }

    private String extractCurlyBlock(String src, int start) {
        int braces = 1;
        int i = start;
        while (braces > 0) {
            if (src.charAt(i) == '{') braces++;
            if (src.charAt(i) == '}') braces--;
            i++;
        }
        return src.substring(start, i - 1);
    }

    private String extractArray(String src, String key) {
    int keyPos = src.indexOf("\"" + key + "\":[");
    if (keyPos == -1) return "";  // <--- key missing, return empty array

    int start = keyPos + key.length() + 3;
    int brackets = 1;
    int i = start;
    while (brackets > 0) {
        if (i >= src.length()) break; // prevent out-of-bounds
        if (src.charAt(i) == '[') brackets++;
        if (src.charAt(i) == ']') brackets--;
        i++;
    }
    return src.substring(start, Math.min(i - 1, src.length()));
}

    private String[] splitObjects(String array) {
        if (array == null || array.isEmpty()) return new String[0];
        return array.split("\\},\\{");
    }


    private String extractString(String key) {
        return extractString(json, key);
    }

    private String extractString(String src, String key) {
        int start = src.indexOf("\"" + key + "\":\"") + key.length() + 3;
        int end = src.indexOf("\"", start);
        return src.substring(start, end);
    }

    private int extractInt(String src, String key) {
        int start = src.indexOf("\"" + key + "\":") + key.length() + 3;
        int end = findEnd(src, start);
        return Integer.parseInt(src.substring(start, end));
    }

    private double extractDouble(String src, String key) {
        int start = src.indexOf("\"" + key + "\":") + key.length() + 3;
        int end = findEnd(src, start);
        return Double.parseDouble(src.substring(start, end));
    }

    private int findEnd(String s, int start) {
        int i = start;
        while (i < s.length() && "0123456789.".indexOf(s.charAt(i)) != -1)
            i++;
        return i;
    }

    private List<String> extractStringArray(String src, String key) {
        String arr = extractArray(src, key);
        List<String> list = new ArrayList<>();
        for (String s : arr.split(",")) {
            list.add(s.replace("\"", ""));
        }
        return list;
    }

    private List<Integer> extractIntArray(String src, String key) {
    String arr = extractArray(src, key);
    List<Integer> list = new ArrayList<>();
    for (String s : arr.split(",")) {
        list.add(Integer.parseInt(s));
    }
    return list;
}
    
    private boolean hasKey(String src, String key) {
        return src.contains("\"" + key + "\":");
    }
}
