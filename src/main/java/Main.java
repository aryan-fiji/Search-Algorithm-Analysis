import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Article> csvData = CSVReader.readCSV("src/main/resources/Article.csv");
        if (csvData.isEmpty()) {
            System.out.println("No data found in CSV file or file not found.");
            return;
        }
        
        // Create both ArrayList and LinkedList from the same data for comparison
        ArrayList<Article> arrayList = new ArrayList<>(csvData);
        LinkedList<Article> linkedList = new LinkedList<>(csvData);

        System.out.println("Total articles loaded: " + arrayList.size());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Search article using ID");
            System.out.println("2. End program");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleArticleSearch(scanner, arrayList, linkedList);
                    break;
                case "2":
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
        
        // Step 1: Choose search algorithm
        int algorithmChoice = displayAlgorithmMenu(scanner);
        if (algorithmChoice == -1) return;
        
        // Step 2: Choose data structure
        int dataStructureChoice = displayDataStructureMenu(scanner);
        if (dataStructureChoice == -1) return;

        long startTime = System.nanoTime();
        Article result = null;
        int index = -1;
        
        // Execute search based on algorithm and data structure choice
        switch (algorithmChoice) {
            case 1: // Linear Search
                if (dataStructureChoice == 1) { 
                    // ArrayList
                    result = SearchAlgorithms.searchById(arrayList, id);
                } 
                else { 
                    // LinkedList
                    result = SearchAlgorithms.searchById(linkedList, id);
                }
                break;
                
            case 2: // Binary Search (requires sorting by ID)
                if (dataStructureChoice == 1) { 
                    // ArrayList
                    ArrayList<Article> sortedArrayList = new ArrayList<>(arrayList);
                    sortedArrayList.sort(Comparator.comparing(Article::getId));
                    index = SearchAlgorithms.binarySearchArrayListById(sortedArrayList, id);
                    result = index != -1 ? sortedArrayList.get(index) : null;
                } 
                else { 
                    // LinkedList
                    LinkedList<Article> sortedLinkedList = new LinkedList<>(linkedList);
                    sortedLinkedList.sort(Comparator.comparing(Article::getId));
                    index = SearchAlgorithms.binarySearchLinkedListById(sortedLinkedList, id);
                    result = index != -1 ? sortedLinkedList.get(index) : null;
                }
                break;
                
            case 3: // Jump Search (requires sorting by ID)
                if (dataStructureChoice == 1) { 
                    // ArrayList
                    ArrayList<Article> jumpArrayList = new ArrayList<>(arrayList);
                    jumpArrayList.sort(Comparator.comparing(Article::getId));
                    index = SearchAlgorithms.jumpSearchArrayListById(jumpArrayList, id);
                    result = index != -1 ? jumpArrayList.get(index) : null;
                } 
                else { 
                    // LinkedList
                    LinkedList<Article> jumpLinkedList = new LinkedList<>(linkedList);
                    jumpLinkedList.sort(Comparator.comparing(Article::getId));
                    index = SearchAlgorithms.jumpSearchLinkedListById(jumpLinkedList, id);
                    result = index != -1 ? jumpLinkedList.get(index) : null;
                }
                break;
                
            case 4: // Exponential Search (requires sorting by ID)
                if (dataStructureChoice == 1) { 
                    // ArrayList
                    ArrayList<Article> expArrayList = new ArrayList<>(arrayList);
                    expArrayList.sort(Comparator.comparing(Article::getId));
                    index = SearchAlgorithms.exponentialSearchArrayListById(expArrayList, id);
                    result = index != -1 ? expArrayList.get(index) : null;
                } 
                else { 
                    // LinkedList
                    LinkedList<Article> expLinkedList = new LinkedList<>(linkedList);
                    expLinkedList.sort(Comparator.comparing(Article::getId));
                    index = SearchAlgorithms.exponentialSearchLinkedListById(expLinkedList, id);
                    result = index != -1 ? expLinkedList.get(index) : null;
                }
                break;
        }
        
        long endTime = System.nanoTime();
        double timeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        
        // Display results
        String algorithmName = getAlgorithmName(algorithmChoice);
        String dataStructureName = dataStructureChoice == 1 ? "ArrayList" : "LinkedList";
        
        if (result != null) {
            System.out.println("\nArticle found using " + algorithmName + " on " + dataStructureName + ":");
            System.out.println(result);
            System.out.printf("Search completed in: %.6f seconds\n", timeInSeconds);
        } 
        else {
            System.out.println("Error: Article not found using " + algorithmName + " on " + dataStructureName + ".");
            System.out.printf("Search completed in: %.6f seconds\n", timeInSeconds);
        }
    }

    private static int displayAlgorithmMenu(Scanner scanner) {
        System.out.println("\nChoose a search algorithm:");
        System.out.println("1. Linear Search");
        System.out.println("2. Binary Search");
        System.out.println("3. Jump Search");
        System.out.println("4. Exponential Search");
        System.out.print("Enter algorithm choice (1-4): ");
        
        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 4) {
                return choice;
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 4.");
            return -1;
        }
    }
    
    private static int displayDataStructureMenu(Scanner scanner) {
        System.out.println("\nChoose a data structure:");
        System.out.println("1. ArrayList");
        System.out.println("2. LinkedList");
        System.out.print("Enter data structure choice (1-2): ");
        
        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 2) {
                return choice;
            } else {
                System.out.println("Invalid choice. Please enter 1 for ArrayList or 2 for LinkedList.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 1 for ArrayList or 2 for LinkedList.");
            return -1;
        }
    }
    
    private static String getAlgorithmName(int algorithmChoice) {
        switch (algorithmChoice) {
            case 1: return "Linear Search";
            case 2: return "Binary Search";
            case 3: return "Jump Search";
            case 4: return "Exponential Search";
            default: return "Unknown Algorithm";
        }
    }
}