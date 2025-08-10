import static spark.Spark.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class WebServer {
    public static void main(String[] args) {
        port(4567);

        // Allow CORS for React to call this
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST");
        });

        // Analyze Logs.txt and return basic stats
        post("/analyze", (req, res) -> {
            try {
                Map<String, List<Double>> eventData = AnalysisEngine.readLogs("Logs.txt");

                List<String> eventNames = new ArrayList<>(eventData.keySet());
                String outputFile = "BaselineStats.txt";

                AnalysisEngine.outputData(eventData, eventNames, outputFile);

                return "Analysis complete. Baseline written to BaselineStats.txt.";
            } catch (Exception e) {
                res.status(500);
                return "Error during analysis: " + e.getMessage();
            }
        });

        // Just a placeholder for alerts
        get("/alerts", (req, res) -> {
            return "No alert system available yet. Placeholder route.";
        });

        get("/status", (req, res) -> "IDS is running");
        get("/baseline", (req, res) -> {
    File file = new File("BaselineStats.txt");
    if (!file.exists()) return "BaselineStats.txt not found";

    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line.trim());
        }
    }

    return lines.stream().skip(1)  // Skip event count
            .map(line -> {
                String[] parts = line.split(":");
                return String.format("{\"event\":\"%s\", \"mean\":%s, \"stddev\":%s}", parts[0], parts[1], parts[2]);
            })
            .collect(Collectors.joining(",", "[", "]"));
});

    }
}
