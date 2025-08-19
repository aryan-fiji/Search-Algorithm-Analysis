import java.io.*;
import java.util.*;
import java.io.InputStreamReader; 

// implement algorithm using arraylist and linkedlist
class Article implements Comparable<Article> {
    private String id;
    private String title;
    private String abstractText;

    public Article(String id, String title, String abstractText) {
        this.id = id;
        this.title = title;
        this.abstractText = abstractText;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAbstractText() { return abstractText; }

    @Override
    public int compareTo(Article other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Article article = (Article) obj;
        return Objects.equals(title, article.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "Article{" + 
               "id='" + id + "', " +
               "title='" + title + "', " + 
               "abstract='" + abstractText + "'}";
    }
}

public class SearchAlgorithms {
    //linear search (array)
    public static <T> int linearSearchArrayList(ArrayList<T> list, T key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(key)) return i;
        }
        return -1;
    }

    //linear search (linkedlist)
    public static <T> int linearSearchLinkedList(LinkedList<T> list, T key) {
        int index = 0;
        for (T item : list) {
            if (item.equals(key)) return index;
            index++;
        }
        return -1;
    }

    //binary search (array)
    public static <T extends Comparable<T>> int binarySearchArrayList(ArrayList<T> list, T key) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = list.get(mid).compareTo(key);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    //binary search (linkedlist)
    public static <T extends Comparable<T>> int binarySearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return binarySearchArrayList(tempList, key);
    }

    //jump search (array)
    public static <T extends Comparable<T>> int jumpSearchArrayList(ArrayList<T> list, T key) {
        int n = list.size();
        int step = (int) Math.floor(Math.sqrt(n));
        int prev = 0;

        while (list.get(Math.min(step, n) - 1).compareTo(key) < 0) {
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1;
        }

        while (prev < Math.min(step, n)) {
            if (list.get(prev).compareTo(key) == 0) return prev;
            prev++;
        }
        return -1;
    }

    //jump search (linkedlist)
    public static <T extends Comparable<T>> int jumpSearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return jumpSearchArrayList(tempList, key);
    }

    //exponential search (array)
    public static <T extends Comparable<T>> int exponentialSearchArrayList(ArrayList<T> list, T key) {
        int n = list.size();
        if (list.get(0).compareTo(key) == 0) return 0;

        int i = 1;
        while (i < n && list.get(i).compareTo(key) <= 0) {
            i *= 2;
        }

        return binarySearchArrayList(new ArrayList<>(list.subList(i / 2, Math.min(i, n))), key);
    }

    //exponential search (linkedlist)
    public static <T extends Comparable<T>> int exponentialSearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return exponentialSearchArrayList(tempList, key);
    }

    // Method to read CSV file and return list of articles
public static List<Article> readCSV(String filename) {
    List<Article> articles = new ArrayList<>();
    
    // Read from Maven resources folder
    InputStream inputStream = SearchAlgorithms.class.getClassLoader()
            .getResourceAsStream(filename);
    
    if (inputStream == null) {
        System.err.println("Could not find file: " + filename + " in resources folder");
        return articles;
    }
    
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        boolean isFirstLine = true;
        
        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            
            String[] values = parseCSVLine(line);
            
            if (values.length >= 3) {
                String id = values[0].trim();
                String title = values[1].trim();  
                String abstractText = values[2].trim();
                
                articles.add(new Article(id, title, abstractText));
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }
    
    return articles;
}

    // Simple CSV line parser that handles basic comma separation
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }

    // ---------- TEST ----------
    public static void main(String[] args) {
        // Read data from CSV file
        List<Article> csvData = readCSV("test.csv");
        
        if (csvData.isEmpty()) {
            System.out.println("No data found in CSV file or file not found.");
            return;
        }
        
        // Create ArrayList and LinkedList from CSV data
        ArrayList<Article> arrayList = new ArrayList<>(csvData);
        LinkedList<Article> linkedList = new LinkedList<>(csvData);
        
        // Sort for binary, jump, and exponential searches
        Collections.sort(arrayList);
        Collections.sort(linkedList);
        
        System.out.println("Loaded " + arrayList.size() + " articles from CSV");
        System.out.println("First few articles:");
        for (int i = 0; i < Math.min(3, arrayList.size()); i++) {
            System.out.println("  " + arrayList.get(i));
        }
        System.out.println();
        
        // Search for the first article as an example
        if (!arrayList.isEmpty()) {
            Article searchKey = arrayList.get(0); // Search for first article
            System.out.println("Searching for: " + searchKey.getTitle());
            System.out.println();
            
            //linear
            System.out.println("Linear ArrayList: " + linearSearchArrayList(arrayList, searchKey));
            System.out.println("Linear LinkedList: " + linearSearchLinkedList(linkedList, searchKey));

            //binary
            System.out.println("Binary ArrayList: " + binarySearchArrayList(arrayList, searchKey));
            System.out.println("Binary LinkedList: " + binarySearchLinkedList(linkedList, searchKey));

            //jump
            System.out.println("Jump ArrayList: " + jumpSearchArrayList(arrayList, searchKey));
            System.out.println("Jump LinkedList: " + jumpSearchLinkedList(linkedList, searchKey));

            //exponential
            System.out.println("Exponential ArrayList: " + exponentialSearchArrayList(arrayList, searchKey));
            System.out.println("Exponential LinkedList: " + exponentialSearchLinkedList(linkedList, searchKey));
        }
    }
}