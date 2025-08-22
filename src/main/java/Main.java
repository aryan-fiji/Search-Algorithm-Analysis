import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Article> csvData = CSVUtils.readCSV("c:/Users/aryan/OneDrive/Documents/CS214/CS214-Assignment-1/CS214-Assignment-1/src/main/resources/Article.csv");
        if (csvData.isEmpty()) {
            System.out.println("No data found in CSV file or file not found.");
            return;
        }
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
        
        int algorithmChoice = displayAlgorithmMenu(scanner);
        if (algorithmChoice == -1) return;

        // Create a target article for comparison-based searches
        Article targetArticle = new Article(id, "", "", 0, 0, 0, 0, 0, 0);
        
        long startTime = System.nanoTime();
        Article result = null;
        int index = -1;
        
        switch (algorithmChoice) {
            case 1: // Linear Search - ArrayList
                result = SearchAlgorithms.searchById(arrayList, id);
                break;
            case 2: // Linear Search - LinkedList
                result = SearchAlgorithms.searchById(linkedList, id);
                break;
            case 3: // Binary Search - ArrayList (requires sorting)
                ArrayList<Article> sortedArrayList = new ArrayList<>(arrayList);
                Collections.sort(sortedArrayList);
                index = SearchAlgorithms.binarySearchArrayList(sortedArrayList, targetArticle);
                result = index != -1 ? sortedArrayList.get(index) : null;
                break;
            case 4: // Binary Search - LinkedList (requires sorting)
                LinkedList<Article> sortedLinkedList = new LinkedList<>(linkedList);
                Collections.sort(sortedLinkedList);
                index = SearchAlgorithms.binarySearchLinkedList(sortedLinkedList, targetArticle);
                result = index != -1 ? sortedLinkedList.get(index) : null;
                break;
            case 5: // Jump Search - ArrayList (requires sorting)
                ArrayList<Article> jumpArrayList = new ArrayList<>(arrayList);
                Collections.sort(jumpArrayList);
                index = SearchAlgorithms.jumpSearchArrayList(jumpArrayList, targetArticle);
                result = index != -1 ? jumpArrayList.get(index) : null;
                break;
            case 6: // Jump Search - LinkedList (requires sorting)
                LinkedList<Article> jumpLinkedList = new LinkedList<>(linkedList);
                Collections.sort(jumpLinkedList);
                index = SearchAlgorithms.jumpSearchLinkedList(jumpLinkedList, targetArticle);
                result = index != -1 ? jumpLinkedList.get(index) : null;
                break;
            case 7: // Exponential Search - ArrayList (requires sorting)
                ArrayList<Article> expArrayList = new ArrayList<>(arrayList);
                Collections.sort(expArrayList);
                index = SearchAlgorithms.exponentialSearchArrayList(expArrayList, targetArticle);
                result = index != -1 ? expArrayList.get(index) : null;
                break;
            case 8: // Exponential Search - LinkedList (requires sorting)
                LinkedList<Article> expLinkedList = new LinkedList<>(linkedList);
                Collections.sort(expLinkedList);
                index = SearchAlgorithms.exponentialSearchLinkedList(expLinkedList, targetArticle);
                result = index != -1 ? expLinkedList.get(index) : null;
                break;
        }
        
        long endTime = System.nanoTime();
        double timeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        
        if (result != null) {
            System.out.println("\nArticle found:");
            System.out.println(result);
            System.out.printf("Search completed in: %.6f seconds\n", timeInSeconds);
        } else {
            System.out.println("Error: Article not found.");
            System.out.printf("Search completed in: %.6f seconds\n", timeInSeconds);
        }
    }

    private static int displayAlgorithmMenu(Scanner scanner) {
        System.out.println("\nChoose a search algorithm:");
        System.out.println("1. Linear Search - ArrayList");
        System.out.println("2. Linear Search - LinkedList");
        System.out.println("3. Binary Search - ArrayList");
        System.out.println("4. Binary Search - LinkedList");
        System.out.println("5. Jump Search - ArrayList");
        System.out.println("6. Jump Search - LinkedList");
        System.out.println("7. Exponential Search - ArrayList");
        System.out.println("8. Exponential Search - LinkedList");
        System.out.print("Enter algorithm choice (1-8): ");
        
        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 8) {
                return choice;
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 8.");
            return -1;
        }
    }
}