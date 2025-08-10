import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalysisEngine {

    public static void main(String[] args) {
        try {
            String inputFile = "Logs.txt"; // Replace with your log file name
            System.out.println("Commencing analysis for " + inputFile + "...\n");

            Map<String, List<Double>> eventData = readLogs(inputFile);
            List<String> eventNames = new ArrayList<>(eventData.keySet());

            String outputFile = "BaselineStats.txt";
            outputData(eventData, eventNames, outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Reads the logs file and extracts event data
    public static Map<String, List<Double>> readLogs(String filename) throws IOException {
        Map<String, List<Double>> eventData = new LinkedHashMap<>();
        List<String> eventNames = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // Read the first line (Day number)
            int noOfEvents = Integer.parseInt(br.readLine().trim()); // Number of events logged

            // Read event names
            for (int i = 0; i < noOfEvents; i++) {
                String eventLine = br.readLine().trim();
                String eventName = eventLine.split(":")[0];
                eventNames.add(eventName);
                eventData.put(eventName, new ArrayList<>());
            }

            // Read daily data
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Day")) { // Start of a new day's data
                    br.readLine(); // Skip number of events line
                    for (String eventName : eventNames) {
                        String eventLine = br.readLine().trim();
                        String[] eventInfo = eventLine.split(":");

                        if (eventInfo[1].equals("D")) { // Discrete event
                            eventData.get(eventName).add(Double.parseDouble(eventInfo[2]));
                        } else if (eventInfo[1].equals("C")) { // Continuous event
                            eventData.get(eventName).add(Double.parseDouble(eventInfo[2]));
                        }
                    }
                }
            }
        }

        return eventData;
    }

    // Outputs data (mean and standard deviation) to a file
    public static void outputData(Map<String, List<Double>> data, List<String> eventNames, String filename) throws IOException {
        List<Double> mean = calculateMean(data);
        List<Double> stddev = calculateStddev(calculateVariance(data, mean));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.valueOf(eventNames.size()));
            for (int i = 0; i < eventNames.size(); i++) {
                writer.write(String.format("\n%s:%.2f:%.2f", eventNames.get(i), mean.get(i), stddev.get(i)));
            }
            System.out.println("Baseline statistics written to " + filename);
        }
    }

    // Calculates the mean for each event
    public static List<Double> calculateMean(Map<String, List<Double>> data) {
        return data.values().stream()
                .map(values -> {
                    double sum = values.stream().mapToDouble(Double::doubleValue).sum();
                    return Math.round((sum / values.size()) * 100.0) / 100.0;
                })
                .collect(Collectors.toList());
    }

    // Calculates the variance for each event
    public static List<Double> calculateVariance(Map<String, List<Double>> data, List<Double> mean) {
        List<Double> variances = new ArrayList<>();
        int index = 0;

        for (List<Double> values : data.values()) {
            double varianceSum = 0.0;
            for (double value : values) {
                varianceSum += Math.pow(value - mean.get(index), 2);
            }
            variances.add(Math.round((varianceSum / values.size()) * 100.0) / 100.0);
            index++;
        }

        return variances;
    }

    // Calculates the standard deviation based on variance
    public static List<Double> calculateStddev(List<Double> variance) {
        return variance.stream()
                .map(var -> Math.round(Math.sqrt(var) * 100.0) / 100.0)
                .collect(Collectors.toList());
    }
}
