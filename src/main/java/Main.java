import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Article> csvData = CSVUtils.readCSV("Article.csv");
        if (csvData.isEmpty()) {
            System.out.println("No data found in CSV file or file not found.");
            return;
        }
        ArrayList<Article> arrayList = new ArrayList<>(csvData);

        System.out.println("Total articles loaded: " + csvData.size());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Search article using ID");
            System.out.println("2. Load details of all articles");
            System.out.println("3. End program");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter ID: ");
                    String input = scanner.nextLine().trim();
                    Article found = SearchAlgorithms.searchById(arrayList, input);
                    if (found != null) {
                        System.out.println("\nArticle found:\n" + found);
                    } else {
                        System.out.println("Error: Article not found.");
                    }
                    break;
                case "2":
                    System.out.println("\nAll articles:");
                    for (Article article : arrayList) {
                        System.out.println(article);
                        System.out.println("-------------------");
                    }
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
}