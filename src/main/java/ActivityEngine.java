import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ActivityEngine {

    // Simulate activity and write to logs file - BASELINE
    public static void simulateActivity(String filename, int days, ArrayList<String> eventData, ArrayList<ArrayList<Double>> dataSet) {
        System.out.println("\nCurrently simulating activity with the data set generated...");

        int noOfEvents = Integer.parseInt(eventData.get(0));

        try (FileWriter fout = new FileWriter(filename, true)) {
            for (int i = 0; i < days; i++) {
                fout.write("Day " + (i + 1) + "\n");
                fout.write(noOfEvents + "\n");

                for (int j = 0; j < noOfEvents; j++) {
                    String[] data = eventData.get(j + 1).split(":");
                    String eventName = data[0];
                    String eventType = data[1];
                    double eventValue = dataSet.get(j).get(i);

                    fout.write(eventName + ":" + eventType + ":" + eventValue + ":\n");
                }

                fout.write("\n");
            }
            System.out.println(".\n.\n.\n" + days + " days of data has been written to " + filename + "!");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Generate data set for each event
    public static ArrayList<ArrayList<Double>> generateDataSet(int days, ArrayList<String> eventData, ArrayList<String> statsData) {
        System.out.println("Currently generating data for " + days + " days of events...");

        int noOfEvents = Integer.parseInt(eventData.get(0));
        ArrayList<ArrayList<Double>> activityData = new ArrayList<>();

        for (int j = 1; j <= noOfEvents; j++) {
            String[] eData = eventData.get(j).split(":");
            String eventType = eData[1];
            int minimum = Integer.parseInt(eData[2]);
            int maximum = Integer.parseInt(eData[3]);

            String[] sData = statsData.get(j).split(":");
            double mean = Double.parseDouble(sData[1]);
            double standardDeviation = Double.parseDouble(sData[2]);

            ArrayList<Double> dataSet = generateData(mean, standardDeviation, days, minimum, maximum, eventType);
            activityData.add(dataSet);
        }

        System.out.println(".\n.\n.\nData set generation completed!");
        return activityData;
    }

    // Generate set of data as close to mean and stdev
    public static ArrayList<Double> generateData(double mean, double standardDeviation, int days, int minimum, int maximum, String eventType) {
        Random random = new Random();
        ArrayList<Double> samples = new ArrayList<>();

        while (true) {
            samples.clear();

            for (int i = 0; i < days; i++) {
                double sample = mean + random.nextGaussian() * standardDeviation;

                if (eventType.equals("D")) { // Discrete event
                    sample = Math.round(sample);
                } else if (eventType.equals("C")) { // Continuous event
                    sample = Math.round(sample * 100.0) / 100.0;
                }

                // Check bounds
                if (sample < minimum || sample > maximum) {
                    i--; // Retry for this sample
                    continue;
                }

                samples.add(sample);
            }

            if (validateSamples(samples, mean, standardDeviation, days)) {
                return samples;
            }
        }
    }

    // Validate samples to ensure mean and standard deviation meet criteria
    private static boolean validateSamples(ArrayList<Double> samples, double mean, double standardDeviation, int days) {
        double sampleMean = samples.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double sampleStdev = calculateStandardDeviation(samples, sampleMean);

        double tolerance = days >= 10 ? 0.05 : 0.1;

        return sampleMean >= mean * (1 - tolerance) && sampleMean <= mean * (1 + tolerance)
                && sampleStdev >= standardDeviation * (1 - tolerance) && sampleStdev <= standardDeviation * (1 + tolerance);
    }

    // Calculate standard deviation
    private static double calculateStandardDeviation(ArrayList<Double> samples, double mean) {
        double variance = samples.stream().mapToDouble(sample -> Math.pow(sample - mean, 2)).sum() / samples.size();
        return Math.sqrt(variance);
    }
}
