import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class Graphs {

    static int comparisonCount;

    // ----- Linear Search -----
    public static int linearSearch(int[] arr, int key) {
        comparisonCount = 0;
        for (int i = 0; i < arr.length; i++) {
            comparisonCount++;
            if (arr[i] == key) return i;
        }
        return -1;
    }

    // ----- Binary Search -----
    public static int binarySearch(int[] arr, int key) {
        comparisonCount = 0;
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            comparisonCount++;
            int mid = (left + right) / 2;
            if (arr[mid] == key) return mid;
            else if (arr[mid] < key) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    // ----- Jump Search -----
    public static int jumpSearch(int[] arr, int key) {
        comparisonCount = 0;
        int n = arr.length;
        int step = (int) Math.sqrt(n);
        int prev = 0;

        // Jump through blocks
        while (arr[Math.min(step, n) - 1] < key) {
            comparisonCount++;
            prev = step;
            step += (int) Math.sqrt(n);
            if (prev >= n) return -1;
        }
        
        // Linear search in the identified block
        for (int i = prev; i < Math.min(step, n); i++) {
            comparisonCount++;
            if (arr[i] == key) return i;
        }
        return -1;
    }

    // ----- Exponential Search -----
    public static int exponentialSearch(int[] arr, int key) {
        comparisonCount = 0;
        
        // Check first element
        if (arr[0] == key) {
            comparisonCount++;
            return 0;
        }
        
        // Find range for binary search by repeated doubling
        int i = 1;
        while (i < arr.length && arr[i] <= key) {
            comparisonCount++;
            i *= 2;
        }
        
        // Perform binary search in the found range
        return binarySearchInRange(arr, i / 2, Math.min(i, arr.length - 1), key);
    }
    
    // Helper method for exponential search - binary search in a specific range
    private static int binarySearchInRange(int[] arr, int left, int right, int key) {
        while (left <= right) {
            comparisonCount++;
            int mid = left + (right - left) / 2;
            if (arr[mid] == key) return mid;
            else if (arr[mid] < key) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    // ---------- Part 2: Race Algorithms (Timing) ----------
    public static void part2_race() {
        int[] sizes = {1000, 5000, 10000, 20000, 50000};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Random rand = new Random();

        System.out.println("Running timing analysis...");
        
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;

            int key = rand.nextInt(n);
            long start, end;

            // Linear Search
            start = System.nanoTime();
            linearSearch(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start), "Linear", String.valueOf(n));

            // Binary Search
            start = System.nanoTime();
            binarySearch(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start), "Binary", String.valueOf(n));

            // Jump Search
            start = System.nanoTime();
            jumpSearch(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start), "Jump", String.valueOf(n));

            // Exponential Search
            start = System.nanoTime();
            exponentialSearch(arr, key);
            end = System.nanoTime();
            dataset.addValue((end - start), "Exponential", String.valueOf(n));
            
            System.out.println("Completed size: " + n);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Part 2: Algorithm Race - Execution Time",
                "Input Size (n)",
                "Execution Time (nanoseconds)",
                dataset
        );
        showChart(chart, "Part 2: Race Results");
    }

    // ---------- Part 3: Empirical Best/Mean/Worst ----------
    public static void part3_empirical() {
        int n = 5000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        Random rand = new Random();

        String[] algos = {"Linear", "Binary", "Jump", "Exponential"};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        System.out.println("Running empirical analysis...");
        
        for (String algo : algos) {
            int[] results = new int[30];
            for (int r = 0; r < 30; r++) {
                // Mix of found and not-found cases
                int key = (r % 10 == 0) ? n + 1 : rand.nextInt(n);
                
                switch (algo) {
                    case "Linear": 
                        linearSearch(arr, key); 
                        break;
                    case "Binary": 
                        binarySearch(arr, key); 
                        break;
                    case "Jump": 
                        jumpSearch(arr, key); 
                        break;
                    case "Exponential": 
                        exponentialSearch(arr, key); 
                        break;
                }
                results[r] = comparisonCount;
            }
            
            int best = Arrays.stream(results).min().getAsInt();
            int worst = Arrays.stream(results).max().getAsInt();
            double mean = Arrays.stream(results).average().orElse(0);

            dataset.addValue(best, "Best", algo);
            dataset.addValue(mean, "Mean", algo);
            dataset.addValue(worst, "Worst", algo);
            
            System.out.println(algo + " - Best: " + best + ", Mean: " + String.format("%.1f", mean) + ", Worst: " + worst);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Part 3: Best, Mean, Worst Case Comparisons",
                "Algorithms",
                "Number of Comparisons",
                dataset
        );
        showChart(chart, "Part 3: Best/Mean/Worst Analysis");
    }

    // ---------- Part 4: Theoretical Complexities ----------
    public static void part4_theory() {
        int[] sizes = {10, 50, 100, 500, 1000, 5000};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        System.out.println("Generating theoretical complexity comparison...");
        
        for (int n : sizes) {
            // Scale theoretical complexities for better visualization
            dataset.addValue(n / 10.0, "Linear O(n)", String.valueOf(n));
            dataset.addValue(Math.log(n) / Math.log(2), "Binary O(log n)", String.valueOf(n));
            dataset.addValue(Math.sqrt(n), "Jump O(√n)", String.valueOf(n));
            dataset.addValue(Math.log(n) / Math.log(2), "Exponential O(log n)", String.valueOf(n));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Part 4: Theoretical Time Complexities",
                "Input Size (n)",
                "Operations (scaled for visualization)",
                dataset
        );
        showChart(chart, "Part 4: Theoretical Complexities");
    }

    // ---------- Utility: Show Chart ----------
    public static void showChart(JFreeChart chart, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Changed from EXIT_ON_CLOSE
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        System.out.println("Starting Search Algorithm Analysis...");
        System.out.println("=========================================");
        
        try {
            // Part 2: Timing analysis
            part2_race();
            Thread.sleep(2000); // Give time to view the first chart
            
            // Part 3: Empirical analysis
            part3_empirical();
            Thread.sleep(2000);
            
            // Part 4: Theoretical comparison
            part4_theory();
            
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
        
        System.out.println("=========================================");
        System.out.println("Analysis complete! Close chart windows when done.");
    }
}