import java.util.*;
import java.util.concurrent.*;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class Main {
    private static final int NUM_RUNS = 30;
    private static final Random random = new Random();
    
    public static void main(String[] args) {
        List<Article> csvData = CSVReader.readCSV("src\\main\\resources\\Article.csv");
        if (csvData.isEmpty()) {
            System.out.println("No data found in CSV file or file not found.");
            return;
        }
        
        // Create both ArrayList and LinkedList from the same data
        ArrayList<Article> arrayList = new ArrayList<>(csvData);
        LinkedList<Article> linkedList = new LinkedList<>(csvData);
        
        // Sort both lists by ID for algorithms that require sorted data
        arrayList.sort(Comparator.comparing(Article::getId));
        linkedList.sort(Comparator.comparing(Article::getId));

        System.out.println("Total articles loaded: " + arrayList.size());
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Search article using ID");
            System.out.println("2. Race all search algorithms (30 runs)");
            System.out.println("3. Generate performance visualization graphs");
            System.out.println("4. Show theoretical complexity curves");
            System.out.println("5. Run integer array algorithm race");
            System.out.println("6. End program");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleArticleSearch(scanner, arrayList, linkedList);
                    break;
                case "2":
                    raceAllAlgorithms(arrayList, linkedList);
                    break;
                case "3":
                    System.out.println("Generating performance visualizations...");
                    Map<String, AlgorithmStats> raceResults = raceAllAlgorithms(arrayList, linkedList);
                    generateAllVisualizations(raceResults, arrayList, linkedList);
                    break;
                case "4":
                    part4_theory();
                    break;
                case "5":
                    part2_race();
                    break;
                case "6":
                    System.out.println("Program ended.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void handleArticleSearch(Scanner scanner, ArrayList<Article> arrayList, LinkedList<Article> linkedList) {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine().trim();
        
        int algorithmChoice = displayAlgorithmMenu(scanner);
        if (algorithmChoice == -1) return;
        
        int dataStructureChoice = displayDataStructureMenu(scanner);
        if (dataStructureChoice == -1) return;

        long startTime = System.nanoTime();
        Article result = null;
        int index = -1;
        
        List<Article> searchList = dataStructureChoice == 1 ? arrayList : linkedList;
        
        switch (algorithmChoice) {
            case 1: // Linear Search
                index = SearchAlgorithms.linearSearch(searchList, id);
                break;

            case 2: // Binary Search
                index = SearchAlgorithms.binarySearch(searchList, id);
                break;

            case 3: // Jump Search
                index = SearchAlgorithms.jumpSearch(searchList, id);
                break;

            case 4: // Exponential Search
                index = SearchAlgorithms.exponentialSearch(searchList, id);
                break;
        }

        if (index != -1) {
            result = searchList.get(index);
        }
        
        long endTime = System.nanoTime();
        double timeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        
        String algorithmName = getAlgorithmName(algorithmChoice);
        String dataStructureName = dataStructureChoice == 1 ? "ArrayList" : "LinkedList";
        
        if (result != null) {
            System.out.println("\nArticle found using " + algorithmName + " on " + dataStructureName + ":");
            System.out.println(result);
            System.out.printf("Search completed in: %.9f seconds\n", timeInSeconds);
        } else {
            System.out.println("Error: Article not found using " + algorithmName + " on " + dataStructureName + ".");
            System.out.printf("Search completed in: %.9f seconds\n", timeInSeconds);
        }
        
        // Performance warning for LinkedList with non-linear algorithms
        if (dataStructureChoice == 2 && algorithmChoice > 1) {
            System.out.println("\nPerformance Note: " + algorithmName + 
                " on LinkedList is inefficient due to O(n) random access time.");
            System.out.println("Consider using ArrayList for better performance with " + algorithmName + ".");
        }
    }

    public static Map<String, AlgorithmStats> raceAllAlgorithms(ArrayList<Article> arrayList, LinkedList<Article> linkedList) {
        System.out.println("\n=== SEARCH ALGORITHMS RACE(" + NUM_RUNS + " RUNS - ALL ALGORITHMS) ===");
        
        // Prepare test keys (mix of existing and non-existing IDs)
        List<String> testKeys = prepareTestKeys(arrayList, NUM_RUNS);
        
        // Result trackers for each algorithm
        Map<String, AlgorithmStats> statsMap = new ConcurrentHashMap<>();
        
        // Define all algorithm combinations to test
        List<AlgorithmConfig> algorithms = Arrays.asList(
            new AlgorithmConfig("Linear - ArrayList", (list, key) -> SearchAlgorithms.linearSearch(list, key)),
            new AlgorithmConfig("Linear - LinkedList", (list, key) -> SearchAlgorithms.linearSearch(list, key)),
            new AlgorithmConfig("Binary - ArrayList", (list, key) -> SearchAlgorithms.binarySearch(list, key)),
            new AlgorithmConfig("Binary - LinkedList", (list, key) -> SearchAlgorithms.binarySearch(list, key)),
            new AlgorithmConfig("Jump - ArrayList", (list, key) -> SearchAlgorithms.jumpSearch(list, key)),
            new AlgorithmConfig("Jump - LinkedList", (list, key) -> SearchAlgorithms.jumpSearch(list, key)),
            new AlgorithmConfig("Exponential - ArrayList", (list, key) -> SearchAlgorithms.exponentialSearch(list, key)),
            new AlgorithmConfig("Exponential - LinkedList", (list, key) -> SearchAlgorithms.exponentialSearch(list, key))
        );      

        for (AlgorithmConfig algo : algorithms) {
            statsMap.put(algo.name, new AlgorithmStats());
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();
        
        for (int run = 0; run < NUM_RUNS; run++) {
            final String currentKey = testKeys.get(run);
            final int currentRun = run;
            
            for (AlgorithmConfig algo : algorithms) {
                futures.add(executor.submit(() -> {
                    long startTime = System.nanoTime();
                    boolean found = false;
                    
                    try {
                        if (algo.name.contains("ArrayList")) {
                            found = algo.searchFunction.search(arrayList, currentKey) != -1;
                        } else {
                            found = algo.searchFunction.search(linkedList, currentKey) != -1;
                        }
                    } catch (Exception e) {
                        System.err.println("Error in " + algo.name + ": " + e.getMessage());
                    }
                    
                    long endTime = System.nanoTime();
                    double timeTaken = (endTime - startTime) / 1_000_000.0; // milliseconds
                    
                    statsMap.get(algo.name).addResult(timeTaken, found, currentRun);
                }));
            }
        }
        
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Error waiting for task: " + e.getMessage());
            }
        }
        
        executor.shutdown();
        
        System.out.println("\n=== RACE RESULTS ===");
        System.out.printf("%-25s %-8s %-8s %-8s %-8s %-8s%n", 
            "Algorithm", "Best(ms)", "Worst(ms)", "Mean(ms)", "Found", "Runs");
        System.out.println("=".repeat(70));
        
        statsMap.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e1.getValue().getMeanTime(), e2.getValue().getMeanTime()))
            .forEach(entry -> {
                AlgorithmStats stats = entry.getValue();
                System.out.printf("%-25s %-8.3f %-8.3f %-8.3f %-8d %-8d%n",
                    entry.getKey(),
                    stats.getBestTime(),
                    stats.getWorstTime(),
                    stats.getMeanTime(),
                    stats.getFoundCount(),
                    stats.getTotalRuns());
            });
        
        // Performance analysis
        System.out.println("\n=== PERFORMANCE ANALYSIS ===");
        analyzeLinkedListPerformance(statsMap);
        
        return statsMap;
    }

    private static void analyzeLinkedListPerformance(Map<String, AlgorithmStats> statsMap) {
        System.out.println("\nLinkedList Performance Analysis:");
        System.out.println("- Linear Search: Optimal for LinkedList (sequential access)");
        System.out.println("- Binary/Jump/Exponential Search: Poor performance due to O(n) random access");
        System.out.println("- ArrayList is recommended for algorithms requiring random access");
        
        // Compare same algorithm performance between data structures
        String[] algorithms = {"Linear", "Binary", "Jump", "Exponential"};
        for (String algo : algorithms) {
            AlgorithmStats arrayStats = statsMap.get(algo + " - ArrayList");
            AlgorithmStats linkedStats = statsMap.get(algo + " - LinkedList");
            
            if (arrayStats != null && linkedStats != null) {
                double ratio = linkedStats.getMeanTime() / arrayStats.getMeanTime();
                System.out.printf("- %s: LinkedList is %.1fx slower than ArrayList%n", algo, ratio);
            }
        }
    }

    // ========== GRAPH GENERATION METHODS (formerly in Graphs.java) ==========
    
    // Main method to generate all visualizations from actual performance data
    public static void generateAllVisualizations(Map<String, AlgorithmStats> performanceData, 
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
    private static void generateRaceChart(Map<String, AlgorithmStats> performanceData) {
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
    private static void generateBestMeanWorstChart(Map<String, AlgorithmStats> performanceData) {
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
    private static void generateDataStructureComparisonChart(Map<String, AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        String[] algorithms = {"Linear", "Binary", "Jump", "Exponential"};
        
        for (String algo : algorithms) {
            AlgorithmStats arrayStats = performanceData.get(algo + " - ArrayList");
            AlgorithmStats linkedStats = performanceData.get(algo + " - LinkedList");
            
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
    private static void generateComplexityAnalysisChart(Map<String, AlgorithmStats> performanceData, int dataSize) {
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
            AlgorithmStats stats = performanceData.get(algo + " - ArrayList");
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
    private static void generateAlgorithmByDataStructureChart(Map<String, AlgorithmStats> performanceData) {
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

    // ========== LEGACY GRAPH METHODS (from original Graphs.java standalone execution) ==========
    
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

    // Method to run the standalone demonstrations (equivalent to old Graphs.main)
    public static void runStandaloneGraphDemos() {
        System.out.println("Running standalone graph demonstrations...");
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

    // ========== UTILITY METHODS ==========
    
    private static List<String> prepareTestKeys(List<Article> articles, int numKeys) {
        List<String> keys = new ArrayList<>();
        int size = articles.size();
        
        for (int i = 0; i < numKeys / 2; i++) {
            int randomIndex = random.nextInt(size);
            keys.add(articles.get(randomIndex).getId());
        }
        
        for (int i = 0; i < numKeys / 2; i++) {
            keys.add("NON_EXISTING_" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        Collections.shuffle(keys);
        return keys;
    }

    private static String getAlgorithmName(int choice) {
        switch (choice) {
            case 1: return "Linear Search";
            case 2: return "Binary Search";
            case 3: return "Jump Search";
            case 4: return "Exponential Search";
            default: return "Unknown";
        }
    }

    private static int displayAlgorithmMenu(Scanner scanner) {
        System.out.println("\nChoose search algorithm:");
        System.out.println("1. Linear Search");
        System.out.println("2. Binary Search");
        System.out.println("3. Jump Search");
        System.out.println("4. Exponential Search");
        System.out.print("Enter choice (1-4 or 0 to cancel): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= 4) return choice;
        } catch (NumberFormatException ignored) {}
        
        System.out.println("Cancelled or invalid choice.");
        return -1;
    }

    private static int displayDataStructureMenu(Scanner scanner) {
        System.out.println("\nChoose data structure:");
        System.out.println("1. ArrayList");
        System.out.println("2. LinkedList");
        System.out.print("Enter choice (1-2 or 0 to cancel): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 1 || choice == 2) return choice;
        } catch (NumberFormatException ignored) {}
        
        System.out.println("Cancelled or invalid choice.");
        return -1;
    }
    
    // ========== INNER CLASSES ==========
    
    public static class AlgorithmConfig {
        String name;
        SearchFunction searchFunction;
        
        AlgorithmConfig(String name, SearchFunction searchFunction) {
            this.name = name;
            this.searchFunction = searchFunction;
        }
    }
    
    private interface SearchFunction {
        int search(List<Article> list, String key);
    }
    
    public static class AlgorithmStats {
        private double bestTime = Double.MAX_VALUE;
        private double worstTime = Double.MIN_VALUE;
        private double totalTime = 0;
        private int foundCount = 0;
        private int totalRuns = 0;
        
        public void addResult(double time, boolean found, int run) {
            bestTime = Math.min(bestTime, time);
            worstTime = Math.max(worstTime, time);
            totalTime += time;
            totalRuns++;
            if (found) foundCount++;
        }
        
        public double getBestTime() { 
            return bestTime; }
        public double getWorstTime() { 
            return worstTime; }
        public double getMeanTime() { 
            return totalRuns > 0 ? totalTime / totalRuns : 0; }
        public int getFoundCount() { 
            return foundCount; }
        public int getTotalRuns() { 
            return totalRuns; }
    }
}