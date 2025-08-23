import java.util.*;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class Graphs {

    // Main method to generate all visualizations from actual performance data
    public static void generateAllVisualizations(Map<String, Main.AlgorithmStats> performanceData, 
                                                ArrayList<Article> arrayList, LinkedList<Article> linkedList) {
        System.out.println("Generating performance visualizations...");
        
        try {
            // 1. Race Results - Mean Time Comparison
            generateRaceChart(performanceData);
            Thread.sleep(1000);
            
            // 2. Best/Mean/Worst Analysis
            generateBestMeanWorstChart(performanceData);
            Thread.sleep(1000);
            
            // 3. ArrayList vs LinkedList Comparison
            generateDataStructureComparisonChart(performanceData);
            Thread.sleep(1000);
            
            // 4. Theoretical vs Empirical Complexity
            generateComplexityAnalysisChart(performanceData, arrayList.size());
            Thread.sleep(1000);
            
            // 5. Algorithm Performance by Data Structure
            generateAlgorithmByDataStructureChart(performanceData);
            
            System.out.println("All visualizations generated successfully!");
            
        } catch (InterruptedException e) {
            System.err.println("Visualization interrupted: " + e.getMessage());
        }
    }

    // 1. Race Results - Show mean performance of all algorithm/data structure combinations
    private static void generateRaceChart(Map<String, Main.AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sort by mean time for better visualization
        performanceData.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e1.getValue().getMeanTime(), e2.getValue().getMeanTime()))
            .forEach(entry -> {
                dataset.addValue(entry.getValue().getMeanTime(), 
                               "Mean Time", entry.getKey());
            });

        JFreeChart chart = ChartFactory.createBarChart(
            "Algorithm Race Results - Mean Execution Time",
            "Algorithm - Data Structure",
            "Mean Time (milliseconds)",
            dataset
        );
        
        showChart(chart, "Race Results - Mean Performance");
    }

    // 2. Best/Mean/Worst case analysis for each algorithm
    private static void generateBestMeanWorstChart(Map<String, Main.AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        performanceData.forEach((algorithmName, stats) -> {
            dataset.addValue(stats.getBestTime(), "Best Case", algorithmName);
            dataset.addValue(stats.getMeanTime(), "Mean Case", algorithmName);
            dataset.addValue(stats.getWorstTime(), "Worst Case", algorithmName);
        });

        JFreeChart chart = ChartFactory.createBarChart(
            "Best, Mean, Worst Case Performance Analysis",
            "Algorithm - Data Structure",
            "Execution Time (milliseconds)",
            dataset
        );
        
        showChart(chart, "Best/Mean/Worst Case Analysis");
    }

    // 3. ArrayList vs LinkedList performance comparison
    private static void generateDataStructureComparisonChart(Map<String, Main.AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        String[] algorithms = {"Linear", "Binary", "Jump", "Exponential"};
        
        for (String algo : algorithms) {
            Main.AlgorithmStats arrayStats = performanceData.get(algo + " - ArrayList");
            Main.AlgorithmStats linkedStats = performanceData.get(algo + " - LinkedList");
            
            if (arrayStats != null && linkedStats != null) {
                dataset.addValue(arrayStats.getMeanTime(), "ArrayList", algo);
                dataset.addValue(linkedStats.getMeanTime(), "LinkedList", algo);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "ArrayList vs LinkedList Performance Comparison",
            "Search Algorithm",
            "Mean Time (milliseconds)",
            dataset
        );
        
        showChart(chart, "Data Structure Comparison");
    }

    // 4. Theoretical complexity vs empirical results
    private static void generateComplexityAnalysisChart(Map<String, Main.AlgorithmStats> performanceData, int dataSize) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Theoretical complexities (normalized for visualization)
        double scaleFactor = 0.001; // Scale theoretical values to match empirical scale
        
        dataset.addValue(dataSize * scaleFactor, "Linear O(n) - Theoretical", "Linear");
        dataset.addValue(Math.log(dataSize) / Math.log(2) * scaleFactor * 10, "Binary O(log n) - Theoretical", "Binary");
        dataset.addValue(Math.sqrt(dataSize) * scaleFactor * 5, "Jump O(√n) - Theoretical", "Jump");
        dataset.addValue(Math.log(dataSize) / Math.log(2) * scaleFactor * 10, "Exponential O(log n) - Theoretical", "Exponential");
        
        // Empirical results (ArrayList only for fair comparison)
        String[] algorithms = {"Linear", "Binary", "Jump", "Exponential"};
        for (String algo : algorithms) {
            Main.AlgorithmStats stats = performanceData.get(algo + " - ArrayList");
            if (stats != null) {
                dataset.addValue(stats.getMeanTime(), algo + " - Empirical", algo);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Theoretical vs Empirical Complexity Analysis (ArrayList)",
            "Search Algorithm",
            "Performance Measure",
            dataset
        );
        
        showChart(chart, "Complexity Analysis");
    }

    // 5. Performance breakdown by algorithm, grouped by data structure
    private static void generateAlgorithmByDataStructureChart(Map<String, Main.AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        performanceData.forEach((key, stats) -> {
            String[] parts = key.split(" - ");
            String algorithm = parts[0];
            String dataStructure = parts[1];
            
            dataset.addValue(stats.getMeanTime(), dataStructure, algorithm);
        });

        JFreeChart chart = ChartFactory.createLineChart(
            "Algorithm Performance by Data Structure",
            "Search Algorithm",
            "Mean Time (milliseconds)",
            dataset
        );
        
        showChart(chart, "Algorithm Performance Trends");
    }

    // Legacy methods for backward compatibility and additional analysis
    public static void part2_race() {
        int[] sizes = {1000, 5000, 10000, 20000, 50000};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Random rand = new Random();

        System.out.println("Running timing analysis on integer arrays...");
        
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;

            int key = rand.nextInt(n);
            long start, end;

            // Linear Search
            start = System.nanoTime();
            linearSearchInt(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start) / 1_000_000.0, "Linear", String.valueOf(n));

            // Binary Search
            start = System.nanoTime();
            binarySearchInt(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start) / 1_000_000.0, "Binary", String.valueOf(n));

            // Jump Search
            start = System.nanoTime();
            jumpSearchInt(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start) / 1_000_000.0, "Jump", String.valueOf(n));

            // Exponential Search
            start = System.nanoTime();
            exponentialSearchInt(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start) / 1_000_000.0, "Exponential", String.valueOf(n));
            
            System.out.println("Completed size: " + n);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Integer Array Algorithm Race - Execution Time",
                "Input Size (n)",
                "Execution Time (milliseconds)",
                dataset
        );
        showChart(chart, "Integer Array Race Results");
    }

    public static void part4_theory() {
        int[] sizes = {10, 50, 100, 500, 1000, 5000};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        System.out.println("Generating theoretical complexity comparison...");
        
        for (int n : sizes) {
            dataset.addValue(n / 100.0, "Linear O(n)", String.valueOf(n));
            dataset.addValue(Math.log(n) / Math.log(2), "Binary O(log n)", String.valueOf(n));
            dataset.addValue(Math.sqrt(n), "Jump O(√n)", String.valueOf(n));
            dataset.addValue(Math.log(n) / Math.log(2), "Exponential O(log n)", String.valueOf(n));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Theoretical Time Complexities",
                "Input Size (n)",
                "Operations (scaled for visualization)",
                dataset
        );
        showChart(chart, "Theoretical Complexities");
    }

    // Integer array search implementations for legacy support
    private static int linearSearchInt(int[] arr, int key) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == key) return i;
        }
        return -1;
    }

    private static int binarySearchInt(int[] arr, int key) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (arr[mid] == key) return mid;
            else if (arr[mid] < key) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    private static int jumpSearchInt(int[] arr, int key) {
        int n = arr.length;
        int step = (int) Math.sqrt(n);
        int prev = 0;

        while (arr[Math.min(step, n) - 1] < key) {
            prev = step;
            step += (int) Math.sqrt(n);
            if (prev >= n) return -1;
        }
        
        for (int i = prev; i < Math.min(step, n); i++) {
            if (arr[i] == key) return i;
        }
        return -1;
    }

    private static int exponentialSearchInt(int[] arr, int key) {
        if (arr[0] == key) return 0;
        
        int i = 1;
        while (i < arr.length && arr[i] <= key) {
            i *= 2;
        }
        
        return binarySearchInRangeInt(arr, i / 2, Math.min(i, arr.length - 1), key);
    }
    
    private static int binarySearchInRangeInt(int[] arr, int left, int right, int key) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == key) return mid;
            else if (arr[mid] < key) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    // Utility method to show charts
    public static void showChart(JFreeChart chart, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        System.out.println("Chart displayed: " + title);
    }

    // Standalone main for testing visualizations
    public static void main(String[] args) {
        System.out.println("Running standalone chart demonstrations...");
        System.out.println("=========================================");
        
        try {
            // Demonstrate theoretical complexities
            part4_theory();
            Thread.sleep(2000);
            
            // Demonstrate integer array race
            part2_race();
            
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
        
        System.out.println("=========================================");
        System.out.println("Demo complete! Close chart windows when done.");
    }
}