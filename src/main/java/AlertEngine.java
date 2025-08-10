import java.io.*;
import java.util.*;
import java.nio.file.*;

public class AlertEngine {

    public static void main(String[] args) {
        while (true) {
            String statsFile = getNewStatsFile();
            int days = getNumberOfDays();
            List<Integer> weights = readWeightsFromEventsFile("Events.txt");
            int threshold = calculateThreshold(weights);

            System.out.println("Threshold calculated: " + threshold);

            List<List<Double>> dailyData = readNewLogs("NewLogs.txt");
            List<Double> means = readMeansFromBaselineFile("Baseline.txt");
            List<Double> stdDevs = readStdDevsFromBaselineFile("Baseline.txt");

            List<Double> dailyTotals = calculateAnomalyCounter(dailyData, weights, means, stdDevs);

            flagAnomalies(dailyTotals, threshold);

            System.out.println("Do you want to repeat the process? (yes/no)");
            Scanner sc = new Scanner(System.in);
            String response = sc.nextLine().trim().toLowerCase();
            if (!response.equals("yes")) {
                System.out.println("Exiting...");
                break;
            }
        }
    }

    public static String getNewStatsFile() {
        Scanner sc = new Scanner(System.in);
        String statsFile;

        while (true) {
            System.out.println("Enter filename for new Stats File: ");
            statsFile = sc.nextLine();
            if (Files.exists(Paths.get(statsFile))) {
                break;
            } else {
                System.out.println("Please enter a correct Stats File again");
            }
        }
        return statsFile;
    }

    public static int getNumberOfDays() {
        Scanner sc = new Scanner(System.in);
        int days;

        while (true) {
            System.out.println("Enter the number of Days: ");
            String input = sc.nextLine();
            try {
                days = Integer.parseInt(input);
                if (days > 0) {
                    break;
                } else {
                    System.out.println("Please enter a number more than 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number");
            }
        }
        return days;
    }

    public static int calculateThreshold(List<Integer> weights) {
        int sum = weights.stream().mapToInt(Integer::intValue).sum();
        return 2 * sum;
    }

    public static List<List<Double>> readNewLogs(String filename) {
        System.out.println("Commencing analysis for " + filename + "...\n");

        List<List<Double>> dailyData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                List<Double> daily = new ArrayList<>();
                int noOfEvents = Integer.parseInt(br.readLine().trim());

                for (int i = 0; i < noOfEvents; i++) {
                    String[] eventData = br.readLine().split(":");
                    daily.add(Double.parseDouble(eventData[2]));
                }

                dailyData.add(daily);
                br.readLine(); // Skip newline
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }

        return dailyData;
    }

public static List<Double> calculateAnomalyCounter(List<List<Double>> dailyData, List<Integer> weights,
                                                   List<Double> means, List<Double> stdDevs) {
    System.out.println("Currently calculating daily totals...\n.\n.\n.");

    List<Double> dailyTotals = new ArrayList<>();

    for (List<Double> dayData : dailyData) {
        double counter = 0.0;

        for (int i = 0; i < dayData.size(); i++) {
            double value = dayData.get(i);
            double deviation = Math.abs(value - means.get(i)) / stdDevs.get(i); // Calculate deviation
            counter += deviation * weights.get(i); // Weight the deviation
        }

        dailyTotals.add(counter); // Add the total anomaly counter for the day
    }

    System.out.println("Daily totals calculated!\n");
    return dailyTotals;
}

    public static void flagAnomalies(List<Double> dailyTotals, int threshold) {
        System.out.println("Currently checking for anomalies...\n.\n.\n.");

        boolean anomaliesDetected = false;

        for (int i = 0; i < dailyTotals.size(); i++) {
            double total = dailyTotals.get(i);
            boolean flagged = total >= threshold;
            System.out.printf("Day %d anomaly count = %.2f %s%n", i + 1, total, flagged ? "--- FLAGGED" : "");

            if (flagged) {
                anomaliesDetected = true;
            }
        }

        if (anomaliesDetected) {
            System.out.println("\nALERT! Anomalies detected!\n");
        } else {
            System.out.println("\nCongratulations! There are no anomalies!\n");
        }
    }

    public static List<Integer> readWeightsFromEventsFile(String filename) {
        // Placeholder for reading weights from the Events file
        return Arrays.asList(1, 2, 3, 4, 5); // Replace with actual file reading logic
    }

    public static List<Double> readMeansFromBaselineFile(String filename) {
        // Placeholder for reading means from the Baseline file
        return Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0); // Replace with actual file reading logic
    }

    public static List<Double> readStdDevsFromBaselineFile(String filename) {
        // Placeholder for reading standard deviations from the Baseline file
        return Arrays.asList(2.0, 3.0, 4.0, 5.0, 6.0); // Replace with actual file reading logic
    }
}
