import java.util.*;

public class SearchAlgorithms {
    //Linear search for ArrayList
    public static <T> int linearSearchArrayList(ArrayList<T> list, T key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(key)) return i;
        }
        return -1;
    }

    //Linear search for LinkedList
    public static <T> int linearSearchLinkedList(LinkedList<T> list, T key) {
        int index = 0;
        for (T item : list) {
            if (item.equals(key)) return index;
            index++;
        }
        return -1;
    }

    //Binary search for ArrayList by ID (requires sorted list0
    public static int binarySearchArrayListById(ArrayList<Article> list, String targetId) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            String midId = list.get(mid).getId();
            int cmp = midId.compareTo(targetId);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;  // Search right half
            else high = mid - 1;              // Search left half
        }
        return -1;
    }

    //Binary search for LinkedList by ID - converts to ArrayList first for random access
    public static int binarySearchLinkedListById(LinkedList<Article> list, String targetId) {
        ArrayList<Article> tempList = new ArrayList<>(list);
        return binarySearchArrayListById(tempList, targetId);
    }

    //Jump search for ArrayList by ID (requires sorted list)
    public static int jumpSearchArrayListById(ArrayList<Article> list, String targetId) {
        int n = list.size();
        int step = (int) Math.floor(Math.sqrt(n));  // Optimal jump size is √n
        int prev = 0;
        
        //Jump through the array
        while (prev < n && list.get(Math.min(step, n) - 1).getId().compareTo(targetId) < 0) {
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1;
        }
        
        // Linear search within identified block
        while (prev < Math.min(step, n)) {
            if (list.get(prev).getId().equals(targetId)) return prev;
            prev++;
        }
        return -1;
    }

    //Jump search for LinkedList by ID (converts to ArrayList first)
    public static int jumpSearchLinkedListById(LinkedList<Article> list, String targetId) {
        ArrayList<Article> tempList = new ArrayList<>(list);
        return jumpSearchArrayListById(tempList, targetId);
    }

    //Exponential search for ArrayList by ID (requires sorted list)
    public static int exponentialSearchArrayListById(ArrayList<Article> list, String targetId) {
        int n = list.size();
        if (list.get(0).getId().equals(targetId)) return 0;
        
        //Find the range where element may be present by doubling the index
        int i = 1;
        while (i < n && list.get(i).getId().compareTo(targetId) <= 0) {
            i *= 2;  // Exponentially increase the bound
        }
        
        //Perform binary search in the found range
        ArrayList<Article> subList = new ArrayList<>(list.subList(i / 2, Math.min(i, n)));
        int result = binarySearchArrayListById(subList, targetId);
        return result != -1 ? result + i / 2 : -1;  // Adjust index for original list
    }

    //Exponential search for LinkedList by ID (converts to ArrayList first)
    public static int exponentialSearchLinkedListById(LinkedList<Article> list, String targetId) {
        ArrayList<Article> tempList = new ArrayList<>(list);
        return exponentialSearchArrayListById(tempList, targetId);
    }

    //Generic binary search for any Comparable type (requires sorted list)
    public static <T extends Comparable<T>> int binarySearchArrayList(ArrayList<T> list, T key) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = list.get(mid).compareTo(key);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;  // Search right half
            else high = mid - 1;             // Search left half
        }
        return -1;
    }

    //Generic binary search for LinkedList (converts to ArrayList first)
    public static <T extends Comparable<T>> int binarySearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return binarySearchArrayList(tempList, key);
    }

    //Generic jump search for ArrayList (requires sorted list)
    public static <T extends Comparable<T>> int jumpSearchArrayList(ArrayList<T> list, T key) {
        int n = list.size();
        int step = (int) Math.floor(Math.sqrt(n));
        int prev = 0;
        
        //Jump through the array in root n steps
        while (list.get(Math.min(step, n) - 1).compareTo(key) < 0) {
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1;
        }
        
        //Linear search within the identified block
        while (prev < Math.min(step, n)) {
            if (list.get(prev).compareTo(key) == 0) return prev;
            prev++;
        }
        return -1;
    }

    //Generic jump search for LinkedList (converts to ArrayList first)
    public static <T extends Comparable<T>> int jumpSearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return jumpSearchArrayList(tempList, key);
    }

    //Generic exponential search for ArrayList (requires sorted list)
    public static <T extends Comparable<T>> int exponentialSearchArrayList(ArrayList<T> list, T key) {
        int n = list.size();
        if (list.get(0).compareTo(key) == 0) return 0;
        
        //Find the range where element may be present by doubling the index
        int i = 1;
        while (i < n && list.get(i).compareTo(key) <= 0) {
            i *= 2;  //Exponentially increase the bound
        }
        
        //Perform binary search in the found range
        return binarySearchArrayList(new ArrayList<>(list.subList(i / 2, Math.min(i, n))), key);
    }

    //Generic exponential search for LinkedList (converts to ArrayList first)
    public static <T extends Comparable<T>> int exponentialSearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return exponentialSearchArrayList(tempList, key);
    }

    //Specific search method for Articles by ID (uses equals comparison on ID field)
    public static Article searchById(List<Article> list, String id) {
        for (Article a : list) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }
}