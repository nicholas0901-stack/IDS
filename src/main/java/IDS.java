import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class IDS {

    public static void main(String[] args) {
        try {
            String eventFile;
            String statsFile;
            int days;

            // Check for command-line arguments
            if (args.length == 3) {
                eventFile = args[0];
                statsFile = args[1];
                days = validateDays(args[2]);
            } else {
                Scanner scanner = new Scanner(System.in);

                // Prompt user for input interactively
                System.out.println("Welcome to IDS");

                eventFile = getFileInput(scanner, "Enter filename for Events: ");
                statsFile = getFileInput(scanner, "Enter filename for Stats: ");
                days = getDaysInput(scanner, "Enter the number of Days: ");
            }

            // Read event and stats files
            List<String> eventData = readFile(eventFile);
            List<String> statsData = readFile(statsFile);

            // Consistency check
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Checking for inconsistencies between " + eventFile + " and " + statsFile + "...");
            System.out.println("--------------------------------------------------------------------");
            if (!consistencyCheck(eventData, statsData)) {
                System.err.println("Inconsistencies detected. Exiting.");
                System.exit(1);
            }
            System.out.println("\nNo inconsistencies found.\n");
            TimeUnit.SECONDS.sleep(1);

            // Process Events
            System.out.println("------------------------");
            System.out.println("Processing " + eventFile + "...");
            System.out.println("------------------------");
            List<Integer> weights = processEvents(eventData);

            // Process Stats
            processStats(statsData);

            // Activity Simulation and Logs
            System.out.println("Simulating activity...");
            ArrayList<ArrayList<Double>> dataSet = generateValidatedDataSet(days, eventData, statsData);
            ActivityEngine.simulateActivity("logs.txt", days, new ArrayList<>(eventData), dataSet);
            System.out.println("\nActivity simulation completed.\n");
            TimeUnit.SECONDS.sleep(1);

            // Analysis Engine
            System.out.println("Starting analysis...");
            Map<String, List<Double>> logData = AnalysisEngine.readLogs("logs.txt");
            List<Double> means = AnalysisEngine.calculateMean(logData);
            List<Double> stddevs = AnalysisEngine.calculateStddev(AnalysisEngine.calculateVariance(logData, means));

            // Write baseline statistics to baseline.txt
            writeBaselineStatistics(statsData);

            System.out.println("\nAnalysis completed. Baseline statistics generated.\n");
            int logCount = 1; // Initialize log count at 1

            // Alert Engine
            while (true) {
                System.out.println("Starting Alert Engine...");
                Scanner scanner = new Scanner(System.in);

                // Prompt user for new stats file and days
                String newStatsFile = getFileInput(scanner, "Enter filename for new Stats File: ");
                int newDays = getDaysInput(scanner, "Enter the number of Days: ");

                // Read new stats and simulate new activity
                List<String> newStatsData = readFile(newStatsFile);
                ArrayList<ArrayList<Double>> newDataSet = generateValidatedDataSet(newDays, eventData, newStatsData);

                // Generate dynamic log file name based on the current date and log count
                String timestamp = new java.text.SimpleDateFormat("ddMMyyyy").format(new java.util.Date());
                String newLogsFile = timestamp + "log" + logCount + ".txt";
                logCount++; // Increment log count after generating the file name

                // Create or overwrite the new log file
                try (PrintWriter writer = new PrintWriter(newLogsFile)) {
                    // Clears the file content if it exists
                } catch (IOException e) {
                    System.err.println("Error creating log file: " + newLogsFile + ". Error: " + e.getMessage());
                    break; // Exit loop if log file creation fails
                }

                // Simulate activity and write logs
                ActivityEngine.simulateActivity(newLogsFile, newDays, new ArrayList<>(eventData), newDataSet);

                // Read and parse new logs
                List<List<Double>> parsedNewLogs = AlertEngine.readNewLogs(newLogsFile);

                // Calculate threshold and anomaly counter
                int threshold = AlertEngine.calculateThreshold(weights);
                List<Double> dailyAnomalies = AlertEngine.calculateAnomalyCounter(parsedNewLogs, weights, means, stddevs);

                // Flag anomalies
                AlertEngine.flagAnomalies(dailyAnomalies, threshold);

                // Notify the user of the log file location
                System.out.println("New logs written to: " + newLogsFile);

                // Option to continue
                System.out.println("Do you want to process another stats file? (yes/no)");
                String response = scanner.nextLine().trim().toLowerCase();
                if (!response.equals("yes")) {
                    System.out.println("Exiting IDS... Goodbye!");
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Helper Methods

    private static int validateDays(String daysStr) {
        try {
            int days = Integer.parseInt(daysStr);
            if (days > 0) {
                return days;
            } else {
                throw new NumberFormatException("Days must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number of days: " + e.getMessage());
            System.exit(1);
            return -1; // Unreachable
        }
    }

    private static String getFileInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            String filename = scanner.nextLine();
            if (Files.exists(Paths.get(filename))) {
                return filename;
            } else {
                System.out.println("File not found. Please enter a valid file name.");
            }
        }
    }

    private static int getDaysInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                int days = Integer.parseInt(scanner.nextLine());
                if (days > 0) {
                    return days;
                } else {
                    System.out.println("Please enter a number greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            System.exit(1);
        }
        return lines;
    }

    private static boolean consistencyCheck(List<String> eventData, List<String> statsData) {
        int noOfEvents = Integer.parseInt(eventData.get(0));
        int noOfStats = Integer.parseInt(statsData.get(0));

        if (noOfEvents != noOfStats) {
            System.err.println("Mismatch in the number of events and stats.");
            return false;
        }

        for (int i = 1; i <= noOfEvents; i++) {
            String eventName = eventData.get(i).split(":")[0];
            String statName = statsData.get(i).split(":")[0];
            if (!eventName.equals(statName)) {
                System.err.println("Mismatch in event and stat names at line " + (i + 1));
                return false;
            }
        }

        return true;
    }

    private static List<Integer> processEvents(List<String> data) {
        int noOfEvents = Integer.parseInt(data.get(0));
        List<Integer> weights = new ArrayList<>();

        for (int i = 1; i <= noOfEvents; i++) {
            String[] eventDetails = data.get(i).split(":");
            String eventName = eventDetails[0];
            String eventType = eventDetails[1];

            String minStr = eventDetails[2].isEmpty() ? "0" : eventDetails[2];
            String maxStr = eventDetails[3].isEmpty()
                ? (eventType.equals("D") ? String.valueOf(Integer.MAX_VALUE) : String.valueOf(Double.MAX_VALUE))
                : eventDetails[3];

            int weight = eventDetails[4].isEmpty() ? 1 : Integer.parseInt(eventDetails[4]);

            double min = Double.parseDouble(minStr);
            double max = Double.parseDouble(maxStr);

            if (!eventType.equals("D") && !eventType.equals("C")) {
                throw new IllegalArgumentException("Invalid event type: " + eventType + ". Must be 'D' or 'C'.");
            }

            System.out.printf("Event: %s, Type: %s, Min: %s, Max: %s, Weight: %d%n", eventName, eventType, min, max, weight);
            weights.add(weight);
        }

        return weights;
    }

    private static void processStats(List<String> data) {
        int noOfEvents = Integer.parseInt(data.get(0));

        for (int i = 1; i <= noOfEvents; i++) {
            String[] statDetails = data.get(i).split(":");
            System.out.printf("Event: %s, Mean: %s, StdDev: %s%n", statDetails[0], statDetails[1], statDetails[2]);
        }
    }

    private static ArrayList<ArrayList<Double>> generateValidatedDataSet(int days, List<String> eventData, List<String> statsData) {
        ArrayList<ArrayList<Double>> dataSet = new ArrayList<>();
        int noOfEvents = Integer.parseInt(eventData.get(0));

        for (int i = 1; i <= noOfEvents; i++) {
            String[] eventDetails = eventData.get(i).split(":");
            String[] statDetails = statsData.get(i).split(":");

            double mean = statDetails[1].isEmpty() ? 0.0
                : Double.parseDouble(statDetails[1]);
            double stdDev = statDetails[2].isEmpty() ? 1.0 : Double.parseDouble(statDetails[2]);
            String eventType = eventDetails[1];

            double min = eventDetails[2].isEmpty() ? 0.0 : Double.parseDouble(eventDetails[2]);
            double max = eventDetails[3].isEmpty()
                ? (eventType.equals("D") ? Integer.MAX_VALUE : Double.MAX_VALUE)
                : Double.parseDouble(eventDetails[3]);

            ArrayList<Double> eventDataForDays = new ArrayList<>();
            Random random = new Random();

            for (int day = 0; day < days; day++) {
                double value;
                do {
                    value = mean + random.nextGaussian() * stdDev;
                    if (eventType.equals("D")) {
                        value = Math.round(value);
                    }
                } while (value < min || value > max);

                eventDataForDays.add(value);
            }

            dataSet.add(eventDataForDays);
        }

        return dataSet;
    }

    private static void writeBaselineStatistics(List<String> statsData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("baseline.txt"))) {
            for (String line : statsData) {
                writer.write(line); // Write each line from statsData as is
                writer.newLine();   // Add a new line
            }
            System.out.println("Baseline file successfully written to match stats file.");
        } catch (IOException e) {
            System.err.println("Error writing baseline statistics: " + e.getMessage());
        }
    }
    
    
    
    
}
