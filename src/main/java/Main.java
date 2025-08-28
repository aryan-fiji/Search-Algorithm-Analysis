import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
// Core interfaces
interface SearchAlgorithm<T> {
    int search(List<T> list, String key);
    String getName();
    String getComplexity();
}

interface DataStructureProvider<T> {
    List<T> getList();
    String getName();
    boolean isOptimalForRandomAccess();
}

interface PerformanceAnalyzer {
    Map<String, AlgorithmStats> runPerformanceTest(List<DataStructureProvider<Article>> dataProviders, 
                                List<SearchAlgorithm<Article>> algorithms, List<String> testKeys);
    void analyzeResults(Map<String, AlgorithmStats> results);
}

interface ChartGenerator {
    void generateAllCharts(Map<String, AlgorithmStats> performanceData, int dataSize);
    void showChart(JFreeChart chart, String title);
}

interface UserInterface {
    void displayMainMenu();
    int getMenuChoice();
    void handleArticleSearch(List<DataStructureProvider<Article>> dataProviders, 
                            List<SearchAlgorithm<Article>> algorithms);
}

// Search algorithm implementations using SearchAlgorithms class
class LinearSearchAdapter implements SearchAlgorithm<Article> {
    @Override
    public int search(List<Article> list, String key) {
        return SearchAlgorithms.linearSearch(list, key);
    }
    @Override
    public String getName() { return "Linear Search"; }

    @Override
    public String getComplexity() { return "O(n)"; }
}

class BinarySearchAdapter implements SearchAlgorithm<Article> {
    @Override
    public int search(List<Article> list, String key) {
        return SearchAlgorithms.binarySearch(list, key);
    }
    @Override
    public String getName() { return "Binary Search"; }
    
    @Override
    public String getComplexity() { return "O(log n)"; }
}

class JumpSearchAdapter implements SearchAlgorithm<Article> {
    @Override
    public int search(List<Article> list, String key) {
        return SearchAlgorithms.jumpSearch(list, key);
    }
    @Override
    public String getName() { return "Jump Search"; }
    
    @Override
    public String getComplexity() { return "O(√n)"; }
}

class ExponentialSearchAdapter implements SearchAlgorithm<Article> {
    @Override
    public int search(List<Article> list, String key) {
        return SearchAlgorithms.exponentialSearch(list, key);
    }
    @Override
    public String getName() { return "Exponential Search"; }
    
    @Override
    public String getComplexity() { return "O(log n)"; }
}

// Data structure provider implementations
class ArrayListProvider<T> implements DataStructureProvider<T> {
    private final List<T> list;
    public ArrayListProvider(List<T> sourceData) {
        this.list = new ArrayList<>(sourceData);
    }
    @Override
    public List<T> getList() { return list; }

    @Override
    public String getName() { return "ArrayList"; }
    
    @Override
    public boolean isOptimalForRandomAccess() { return true; }
}

class LinkedListProvider<T> implements DataStructureProvider<T> {
    private final List<T> list;
    
    public LinkedListProvider(List<T> sourceData) {
        this.list = new LinkedList<>(sourceData);
    }
    @Override
    public List<T> getList() { return list; }
    
    @Override
    public String getName() { return "LinkedList"; }
    
    @Override
    public boolean isOptimalForRandomAccess() { return false; }
}

// Performance analyzer implementation
class ConcurrentPerformanceAnalyzer implements PerformanceAnalyzer {
    private static final int NUM_RUNS = 30;
    
    @Override
    public Map<String, AlgorithmStats> runPerformanceTest(List<DataStructureProvider<Article>> dataProviders,
                                List<SearchAlgorithm<Article>> algorithms, List<String> testKeys) {
        System.out.println("\nSearch Algorithms Race(" + NUM_RUNS + " Runs - All 8 Algorithms) ===");
        Map<String, AlgorithmStats> statsMap = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();
        
        // Initialize stats for all combinations
        for (DataStructureProvider<Article> provider : dataProviders) {
            for (SearchAlgorithm<Article> algorithm : algorithms) {
                String key = algorithm.getName() + " - " + provider.getName();
                statsMap.put(key, new AlgorithmStats());
            }
        }
        
        // Run performance tests
        for (int run = 0; run < testKeys.size() && run < NUM_RUNS; run++) {
            final String currentKey = testKeys.get(run);
            final int currentRun = run;
            
            for (DataStructureProvider<Article> provider : dataProviders) {
                for (SearchAlgorithm<Article> algorithm : algorithms) {
                    futures.add(executor.submit(() -> {
                        String statsKey = algorithm.getName() + " - " + provider.getName();
                        long startTime = System.nanoTime();
                        boolean found = false;
                        
                        try {
                            int result = algorithm.search(provider.getList(), currentKey);
                            found = result != -1;
                        } 
                        catch (Exception e) {
                            System.err.println("Error in " + statsKey + ": " + e.getMessage());
                        }
                        long endTime = System.nanoTime();
                        double timeTaken = (endTime - startTime) / 1_000_000.0; // milliseconds
                        statsMap.get(statsKey).addResult(timeTaken, found, currentRun);
                    }));
                }
            }
        }
        
        // Wait for task completion
        for (Future<?> future : futures) {
            try {
                future.get();
            } 
            catch (InterruptedException e) {
                System.err.println("Task interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            catch (java.util.concurrent.ExecutionException e) {
                System.err.println("Execution error: " + e.getMessage());
            }
        }
        executor.shutdown();
        return statsMap;
    }
    
    @Override
    public void analyzeResults(Map<String, AlgorithmStats> results) {
        System.out.println("\nAlgorithm Race Performance Summary:");
        System.out.printf("%-30s %-10s %-10s %-10s %-8s %-8s%n", 
            "Algorithm", "Best(ms)", "Worst(ms)", "Mean(ms)", "Found", "Runs");
        System.out.println("=".repeat(80));
        results.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e1.getValue().getMeanTime(), e2.getValue().getMeanTime()))
            .forEach(entry -> {
                AlgorithmStats stats = entry.getValue();
                System.out.printf("%-30s %-10.3f %-10.3f %-10.3f %-8d %-8d%n",
                    entry.getKey(),
                    stats.getBestTime(),
                    stats.getWorstTime(),
                    stats.getMeanTime(),
                    stats.getFoundCount(),
                    stats.getTotalRuns());
            });
        analyzeDataStructurePerformance(results);
    }
    
    private void analyzeDataStructurePerformance(Map<String, AlgorithmStats> statsMap) {
        System.out.println("\nAlgorithm Performance by Data Structure Analysis:");
        System.out.println("LinkedList Performance Analysis:");
        System.out.println("- Linear Search: Optimal for LinkedList (sequential access)");
        System.out.println("- Binary/Jump/Exponential Search: Poor performance due to O(n) random access");
        System.out.println("- ArrayList is recommended for algorithms requiring random access");
        
        // Compare same algorithm performance between data structures
        String[] algorithms = {"Linear Search", "Binary Search", "Jump Search", "Exponential Search"};
        for (String algo : algorithms) {
            AlgorithmStats arrayStats = statsMap.get(algo + " - ArrayList");
            AlgorithmStats linkedStats = statsMap.get(algo + " - LinkedList");
            
            if (arrayStats != null && linkedStats != null) {
                double ratio = linkedStats.getMeanTime() / arrayStats.getMeanTime();
                System.out.printf("- %s: LinkedList is %.1fx slower than ArrayList%n", algo, ratio);
            }
        }
    }
}

// Chart generator implementation
class JFreeChartGenerator implements ChartGenerator {
    @Override
    public void generateAllCharts(Map<String, AlgorithmStats> performanceData, int dataSize) {
        System.out.println("Generating performance visualizations...");
        generateRaceChart(performanceData);
        generateBestMeanWorstChart(performanceData);
        generateDataStructureComparisonChart(performanceData);
        generateComplexityAnalysisChart(performanceData, dataSize);
        generateAlgorithmByDataStructureChart(performanceData);
        System.out.println("All visualizations generated successfully!");
    }
    
    private void generateRaceChart(Map<String, AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
        performanceData.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e1.getValue().getMeanTime(), e2.getValue().getMeanTime()))
            .forEach(entry -> {
                dataset.addValue(entry.getValue().getMeanTime(), 
                               "Mean Time", entry.getKey());
            });

        JFreeChart chart = ChartFactory.createBarChart(
            "Algorithm Race Results - Mean Execution Time",
            "Algorithm - Data Structure",
            "Mean Time (in milliseconds)",
            dataset
        );
        showChart(chart, "Race Results - Mean Performance");
    }
    
    private void generateBestMeanWorstChart(Map<String, AlgorithmStats> performanceData) {
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
    
    private void generateDataStructureComparisonChart(Map<String, AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] algorithms = {"Linear Search", "Binary Search", "Jump Search", "Exponential Search"};
        
        for (String algo : algorithms) {
            AlgorithmStats arrayStats = performanceData.get(algo + " - ArrayList");
            AlgorithmStats linkedStats = performanceData.get(algo + " - LinkedList");
            
            if (arrayStats != null && linkedStats != null) {
                dataset.addValue(arrayStats.getMeanTime(), "ArrayList", algo.replace(" Search", ""));
                dataset.addValue(linkedStats.getMeanTime(), "LinkedList", algo.replace(" Search", ""));
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
    
    private void generateComplexityAnalysisChart(Map<String, AlgorithmStats> performanceData, int dataSize) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double scaleFactor = 0.001;
        dataset.addValue(dataSize * scaleFactor, "Linear O(n) - Theoretical", "Linear");
        dataset.addValue(Math.log(dataSize) / Math.log(2) * scaleFactor * 10, "Binary O(log n) - Theoretical", "Binary");
        dataset.addValue(Math.sqrt(dataSize) * scaleFactor * 5, "Jump O(√n) - Theoretical", "Jump");
        dataset.addValue(Math.log(dataSize) / Math.log(2) * scaleFactor * 10, "Exponential O(log n) - Theoretical", "Exponential");
        
        String[] algorithms = {"Linear Search", "Binary Search", "Jump Search", "Exponential Search"};
        for (String algo : algorithms) {
            AlgorithmStats stats = performanceData.get(algo + " - ArrayList");
            if (stats != null) {
                dataset.addValue(stats.getMeanTime(), algo.replace(" Search", "") + " - Empirical", algo.replace(" Search", ""));
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
    
    private void generateAlgorithmByDataStructureChart(Map<String, AlgorithmStats> performanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        performanceData.forEach((key, stats) -> {
            String[] parts = key.split(" - ");
            if (parts.length == 2) {
                String algorithm = parts[0];
                String dataStructure = parts[1];
                dataset.addValue(stats.getMeanTime(), dataStructure, algorithm.replace(" Search", ""));
            }
        });
        JFreeChart chart = ChartFactory.createLineChart(
            "Algorithm Performance by Data Structure",
            "Search Algorithm",
            "Mean Time (milliseconds)",
            dataset
        );
        showChart(chart, "Algorithm Performance Trends");
    }
    @Override
    public void showChart(JFreeChart chart, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println("Chart displayed: " + title);
    }
}

// User interface implementation
class ConsoleUserInterface implements UserInterface {
    private static final String INVALID_CHOICE_MSG = "Cancelled or invalid choice.";
    private final Scanner scanner;
    public ConsoleUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public String getInput() {
        return scanner.nextLine();
    }
    
    @Override
    public void displayMainMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Search for an article using ID");
        System.out.println("2. Race all search algorithms (30 runs - Random Values)");
        System.out.println("3. Generate search algorithms performance visualization graphs");
        System.out.println("4. Show theoretical complexities comparison graph");
        System.out.println("5. End program");
        System.out.print("Enter choice: ");
    }
    
    @Override
    public int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    @Override
    public void handleArticleSearch(List<DataStructureProvider<Article>> dataProviders, 
                                  List<SearchAlgorithm<Article>> algorithms) {
        System.out.print("Enter Article ID: ");
        String id = scanner.nextLine().trim();
        
        int algorithmChoice = getChoice("Choose search algorithm:", 
            Arrays.asList("Linear Search", "Binary Search", "Jump Search", "Exponential Search"), 1, 4);
        if (algorithmChoice == -1) return;
        
        int dataStructureChoice = getChoice("Choose data structure:", 
            Arrays.asList("ArrayList", "LinkedList"), 1, 2);
        if (dataStructureChoice == -1) return;
        
        SearchAlgorithm<Article> selectedAlgorithm = algorithms.get(algorithmChoice - 1);
        DataStructureProvider<Article> selectedProvider = dataProviders.get(dataStructureChoice - 1); 
        performSearch(id, selectedAlgorithm, selectedProvider);
    }
    
    private int getChoice(String prompt, List<String> options, int minChoice, int maxChoice) {
        System.out.println("\n" + prompt);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.print("Enter choice (" + minChoice + "-" + maxChoice + " or 0 to cancel): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= minChoice && choice <= maxChoice) return choice;
        } catch (NumberFormatException ignored) {}
        System.out.println(INVALID_CHOICE_MSG);
        return -1;
    }
    
    private void performSearch(String id, SearchAlgorithm<Article> algorithm, 
                              DataStructureProvider<Article> provider) {
        long startTime = System.nanoTime();
        int index = algorithm.search(provider.getList(), id);
        long endTime = System.nanoTime();
        double timeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        
        if (index != -1) {
            Article result = provider.getList().get(index);
            System.out.println("\nArticle found using " + algorithm.getName() + " on " + provider.getName() + ":");
            System.out.println(result);
        } else {
            System.out.println("Error: Article not found using " + algorithm.getName() + " on " + provider.getName() + ".");
        }
        System.out.printf("Search completed in: %.9f seconds\n", timeInSeconds);
        
        // Performance warning for inefficient combinations
        if (!provider.isOptimalForRandomAccess() && !algorithm.getName().equals("Linear Search")) {
            System.out.println("\nPerformance Note: " + algorithm.getName() + 
                " on " + provider.getName() + " is inefficient due to O(n) random access time.");
            System.out.println("Consider using ArrayList for better performance with " + algorithm.getName() + ".");
        }
    }
    public void close() { // Close scanner
        scanner.close();
    }
}

// Main class
public class Main {
    // Components
    private final UserInterface userInterface;
    private final PerformanceAnalyzer performanceAnalyzer;
    private final ChartGenerator chartGenerator;

    // Data and algorithm providers
    private final List<DataStructureProvider<Article>> dataProviders;
    private final List<SearchAlgorithm<Article>> algorithms;

    // Store last race results
    private Map<String, AlgorithmStats> lastRaceResults = null;

    public Main() {
        this.userInterface = new ConsoleUserInterface();
        this.performanceAnalyzer = new ConcurrentPerformanceAnalyzer();
        this.chartGenerator = new JFreeChartGenerator();
        this.dataProviders = new ArrayList<>();
        this.algorithms = Arrays.asList(
            new LinearSearchAdapter(),
            new BinarySearchAdapter(),
            new JumpSearchAdapter(),
            new ExponentialSearchAdapter()
        );
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        System.out.println("--- CS214 Assignment 1 (S11230987 & S11230995) ---");
        System.out.println("Loading data from CSV File...");
        
        // Load and initialize data
        List<Article> sortedData = loadAndSortData();
        if (sortedData.isEmpty()) {
            System.out.println("No data found. Please check Article.csv location.");
            return;
        }

        // Initialize data providers with shared sorted data
        dataProviders.add(new ArrayListProvider<>(sortedData));
        dataProviders.add(new LinkedListProvider<>(sortedData));
        System.out.println("Total articles loaded: " + sortedData.size());

        // Main application loop
        while (true) {
            userInterface.displayMainMenu();
            int choice = userInterface.getMenuChoice();
            switch (choice) {
                case 1 -> userInterface.handleArticleSearch(dataProviders, algorithms);
                case 2 -> {
                    lastRaceResults = runPerformanceRace(sortedData);
                }
                case 3 -> {
    System.out.println("Do you want to use the results from the last race (Option 2)?");
    System.out.print("Enter 1 to use values from the previous search algorithm race or 2 to run a new test: ");
    
    // Get the scanner from the existing userInterface instead of creating a new one
    String input = ((ConsoleUserInterface) userInterface).getInput().trim().toUpperCase();
    
    Map<String, AlgorithmStats> raceResults;
    if ("1".equals(input) && lastRaceResults != null) {
        raceResults = lastRaceResults;
    } else {
        raceResults = runPerformanceRace(sortedData);
        lastRaceResults = raceResults;
    }
    chartGenerator.generateAllCharts(raceResults, sortedData.size());
}
                case 4 -> generateTheoreticalComplexityCharts();
                case 5 -> {
                    System.out.println("--- Program ended. ---");
                    closeResources();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    //Loads CSV data with portable resource loading
    private List<Article> loadAndSortData() {
        List<Article> csvData = new ArrayList<>();
        
        // Method 1: Try to load from Maven resources (portable)
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Article.csv");
            if (inputStream != null) {
                System.out.println("Loading CSV from resources...");
                
                // Create temporary file from resource
                File tempFile = File.createTempFile("Article", ".csv");
                tempFile.deleteOnExit();
                
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                inputStream.close();
                
                // Use existing CSVReader with temp file
                csvData = CSVReader.readCSV(tempFile.getAbsolutePath());
                if (!csvData.isEmpty()) {
                    System.out.println("Successfully loaded " + csvData.size() + " articles from resources");
                }
            } else {
                System.out.println("Article.csv not found in resources, trying file system...");
            }
        } catch (IOException e) {
            System.out.println("Could not load from resources: " + e.getMessage());
        }
        
        // Method 2: Fallback to file system
        if (csvData.isEmpty()) {
            System.out.println("Trying file system locations...");
            String[] possiblePaths = {
                "Article.csv",
                "src/main/resources/Article.csv",
                "target/classes/Article.csv",
                "./Article.csv",
                "data/Article.csv"
            };
            
            for (String path : possiblePaths) {
                File file = new File(path);
                System.out.println("   Checking: " + file.getAbsolutePath() + 
                                 " - " + (file.exists() ? "Exists" : "Not Found"));
                if (file.exists() && file.canRead()) {
                    csvData = CSVReader.readCSV(path);
                    if (!csvData.isEmpty()) {
                        System.out.println("Successfully loaded from: " + path);
                        break;
                    }
                }
            }
        }
        
        // Method 3: Show debugging info if still not found
        if (csvData.isEmpty()) {
            System.out.println("\nCSV File not found!");
            printDebuggingInfo();
            return csvData;
        }
        
        // Sort the data
        csvData.sort(Comparator.comparing(Article::getId));
        System.out.println("Data sorted by ID for binary search compatibility");
        return csvData;
    }
    
    private void printDebuggingInfo() {
        System.out.println("\n--- Debugging Information ---");
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        
        // Show where we're running from
        try {
            String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            System.out.println("Application running from: " + jarPath);
        } catch (Exception e) {
            System.out.println("Could not determine application location");
        }
        
        // List current directory contents
        System.out.println("\nFiles in current directory:");
        File currentDir = new File(".");
        File[] files = currentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println("  " + file.getName() + (file.isDirectory() ? " [DIR]" : ""));
            }
        }
        
        System.out.println("\n--- Problem Fix Suggestions: ---");
        System.out.println("For Maven projects, put Article.csv in: src/main/resources/Article.csv");
        System.out.println("Then run: mvn clean package");
        System.out.println("Then run: java -jar target/search-algorithms-portable.jar");
    }
    
    private Map<String, AlgorithmStats> runPerformanceRace(List<Article> data) {
        int numKeys = 30;
        List<String> testKeys = prepareTestKeys(data, numKeys);

        Map<String, AlgorithmStats> statsMap = new ConcurrentHashMap<>();
        for (DataStructureProvider<Article> provider : dataProviders) {
            for (SearchAlgorithm<Article> algorithm : algorithms) {
                String key = algorithm.getName() + " - " + provider.getName();
                statsMap.put(key, new AlgorithmStats());
            }
        }

        System.out.println("\n--- Mid-Computation Information ---");
        for (int i = 0; i < testKeys.size(); i++) {
            String searchKey = testKeys.get(i);
            System.out.println("Random Element " + (i + 1) + ": " + searchKey);
            boolean foundInAny = false;
            for (SearchAlgorithm<Article> algorithm : algorithms) {
                for (DataStructureProvider<Article> provider : dataProviders) {
                    String statsKey = algorithm.getName() + " - " + provider.getName();
                    long startTime = System.nanoTime();
                    boolean found = false;
                    
                    try {
                        int result = algorithm.search(provider.getList(), searchKey);
                        found = result != -1;
                    } catch (Exception e) {
                        System.err.println("Error in " + statsKey + ": " + e.getMessage());
                    }
                    long endTime = System.nanoTime();
                    double timeTaken = (endTime - startTime) / 1_000_000.0; // ms
                    statsMap.get(statsKey).addResult(timeTaken, found, i);
                    
                    if (found) foundInAny = true;
                    System.out.printf("%-28s %-14s %-25s %-10s %-10.3f%n",
                        algorithm.getName(),
                        provider.getName(),
                        searchKey,
                        found ? "Yes" : "No",
                        timeTaken
                    );
                }
            }
            if (!foundInAny) {
                System.out.println("Value " + searchKey + " is not present in the CSV file");
            }
            System.out.println(); // Blank line between each key group
        }
        performanceAnalyzer.analyzeResults(statsMap);
        return statsMap;
    }
    
    private void generateTheoreticalComplexityCharts() {
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
        chartGenerator.showChart(chart, "Theoretical Complexities");
    }
    
    private List<String> prepareTestKeys(List<Article> articles, int numKeys) {
        List<String> keys = new ArrayList<>();
        Random random = new Random();
        int size = articles.size();

        // Half existing keys
        Set<String> usedIds = new HashSet<>();
        while (usedIds.size() < numKeys / 2) {
            int randomIndex = random.nextInt(size);
            String id = articles.get(randomIndex).getId();
            if (usedIds.add(id)) {
                keys.add(id);
            }
        }

        // Half non-existing keys (just random numbers as strings, not prefixed)
        Set<String> existingIds = new HashSet<>();
        for (Article a : articles) existingIds.add(a.getId());
        int nonExistingCount = 0;
        while (nonExistingCount < numKeys / 2) {
            String candidate = String.valueOf(random.nextInt(1000000));
            if (!existingIds.contains(candidate)) {
                keys.add(candidate);
                nonExistingCount++;
            }
        }
        Collections.shuffle(keys);
        return keys;
    }

    private void closeResources() {
        if (userInterface instanceof ConsoleUserInterface cui) {
            cui.close();
        }
    }
}

//hold statistics for each algorithm-data structure combination
class AlgorithmStats {
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
        return bestTime == Double.MAX_VALUE ? 0 : bestTime; 
    }
    public double getWorstTime() { 
        return worstTime == Double.MIN_VALUE ? 0 : worstTime; 
    }
    public double getMeanTime() { 
        return totalRuns > 0 ? totalTime / totalRuns : 0; 
    }
    public int getFoundCount() { 
        return foundCount; 
    }
    public int getTotalRuns() { 
        return totalRuns; 
    }
}