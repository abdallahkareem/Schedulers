import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonLoader {
    public static Gson gson = new Gson();

    // Load Schedular Testcase from JSON file
    public static SchedularTestCase loadSchedularTestcase(String path) {
        try(FileReader reader = new FileReader(path)){
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            SchedularTestCase SCTestcase = new SchedularTestCase();
            // get schedular testcase details
            SCTestcase.testname = root.get("name").getAsString();
            // get the inputs details
            JsonObject input = root.getAsJsonObject("input");
            SCTestcase.contextSwitch = input.get("contextSwitch").getAsInt();
            SCTestcase.rrQuantum = input.get("rrQuantum").getAsInt();
            SCTestcase.agingInterval = input.get("agingInterval").getAsInt();
            SCTestcase.processes = gson.fromJson(input.get("processes"), 
                                            new com.google.gson.reflect.TypeToken<java.util.List<Process>>(){}.getType());
            // get the expected output details
            JsonObject expectedOutput = root.getAsJsonObject("expectedOutput");
            SCTestcase.expectedOutput = gson.fromJson(expectedOutput, ExpectedOutput.class);
            return SCTestcase;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // Load AG Testcase from JSON file
    public static AGTestCase loadAGTestcase(String path) {
        try(FileReader reader = new FileReader(path)){
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            AGTestCase AGTestcase = new AGTestCase();
            // get AG testcase inputs details
            JsonObject input = root.getAsJsonObject("input");
            AGTestcase.processes = gson.fromJson(input.get("processes"), 
                                            new com.google.gson.reflect.TypeToken<java.util.List<Process>>(){}.getType());
            // get the expected output details
            JsonObject expectedOutput = root.getAsJsonObject("expectedOutput");
            AGTestcase.expectedOutput = gson.fromJson(expectedOutput, ExpectedOutput.class);
            return AGTestcase;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
