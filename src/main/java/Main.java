import java.util.*;
import java.util.concurrent.*;

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
            System.out.println("3. End program");
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
    }

    private static void raceAllAlgorithms(ArrayList<Article> arrayList, LinkedList<Article> linkedList) {
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
    }

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
    
    private static class AlgorithmConfig {
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
    
    private static class AlgorithmStats {
        private double bestTime = Double.MAX_VALUE;
        private double worstTime = Double.MIN_VALUE;
        private double totalTime = 0;
        private int foundCount = 0;
        private int totalRuns = 0;
        
        void addResult(double time, boolean found, int run) {
            bestTime = Math.min(bestTime, time);
            worstTime = Math.max(worstTime, time);
            totalTime += time;
            totalRuns++;
            if (found) foundCount++;
        }
        
        double getBestTime() { 
            return bestTime; }
        double getWorstTime() { 
            return worstTime; }
        double getMeanTime() { 
            return totalRuns > 0 ? totalTime / totalRuns : 0; }
        int getFoundCount() { 
            return foundCount; }
        int getTotalRuns() { 
            return totalRuns; }
    }
}
