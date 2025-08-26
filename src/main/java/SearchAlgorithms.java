import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchAlgorithms {
    
    // Linear Search
    public static int linearSearchArrayList(ArrayList<Article> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(key)) return i;
        }
        return -1;
    }

    public static int linearSearchLinkedList(LinkedList<Article> list, String key) {
        int index = 0;
        for (Article article : list) {
            if (article.getId().equals(key)) return index;
            index++;
        }
        return -1;
    }

    // Binary Search
    public static int binarySearchArrayList(ArrayList<Article> list, String key) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = list.get(mid).getId().compareTo(key);
            if (cmp == 0) return mid;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    public static int binarySearchLinkedList(LinkedList<Article> list, String key) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            // Accessing LinkedList element by index is O(n)
            int cmp = list.get(mid).getId().compareTo(key);
            if (cmp == 0) return mid;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    // Jump Search
    public static int jumpSearchArrayList(ArrayList<Article> list, String key) {
        int n = list.size();
        if (n == 0) return -1;
        int step = (int) Math.sqrt(n);
        int prev = 0;

        while (prev < n && list.get(Math.min(step, n) - 1).getId().compareTo(key) < 0) {
            prev = step;
            step += (int) Math.sqrt(n);
            if (prev >= n) return -1;
        }

        while (prev < Math.min(step, n)) {
            if (list.get(prev).getId().equals(key)) return prev;
            prev++;
        }
        return -1;
    }

    public static int jumpSearchLinkedList(LinkedList<Article> list, String key) {
        int n = list.size();
        if (n == 0) return -1;
        int step = (int) Math.sqrt(n);
        int prev = 0;

        while (prev < n && list.get(Math.min(step, n) - 1).getId().compareTo(key) < 0) {
            prev = step;
            step += (int) Math.sqrt(n);
            if (prev >= n) return -1;
        }

        while (prev < Math.min(step, n)) {
            if (list.get(prev).getId().equals(key)) return prev;
            prev++;
        }
        return -1;
    }

    // Exponential Search
    public static int exponentialSearchArrayList(ArrayList<Article> list, String key) {
        int n = list.size();
        if (n == 0) return -1;
        if (list.get(0).getId().equals(key)) return 0;
        int i = 1;

        while (i < n && list.get(i).getId().compareTo(key) <= 0) {
            i *= 2;
        }
        int result = binarySearchArrayList(new ArrayList<>(list.subList(i / 2, Math.min(i, n))), key);
        return result != -1 ? result + (i / 2) : -1;
    }

    public static int exponentialSearchLinkedList(LinkedList<Article> list, String key) {
        int n = list.size();
        if (n == 0) return -1;
        if (list.get(0).getId().equals(key)) return 0;
        int i = 1;

        while (i < n && list.get(i).getId().compareTo(key) <= 0) {
            i *= 2;
        }
        int result = binarySearchLinkedList(new LinkedList<>(list.subList(i / 2, Math.min(i, n))), key);
        return result != -1 ? result + (i / 2) : -1;
    }

    // Overloaded methods for List<Article>
    public static int linearSearch(List<Article> list, String key) {
        if (list instanceof ArrayList)
            return linearSearchArrayList((ArrayList<Article>) list, key);
        else if (list instanceof LinkedList)
            return linearSearchLinkedList((LinkedList<Article>) list, key);
        else
            throw new IllegalArgumentException("Unsupported List type");
    }

    public static int binarySearch(List<Article> list, String key) {
        if (list instanceof ArrayList)
            return binarySearchArrayList((ArrayList<Article>) list, key);
        else if (list instanceof LinkedList)
            return binarySearchLinkedList((LinkedList<Article>) list, key);
        else
            throw new IllegalArgumentException("Unsupported List type");
    }

    public static int jumpSearch(List<Article> list, String key) {
        if (list instanceof ArrayList)
            return jumpSearchArrayList((ArrayList<Article>) list, key);
        else if (list instanceof LinkedList)
            return jumpSearchLinkedList((LinkedList<Article>) list, key);
        else
            throw new IllegalArgumentException("Unsupported List type");
    }

    public static int exponentialSearch(List<Article> list, String key) {
        if (list instanceof ArrayList)
            return exponentialSearchArrayList((ArrayList<Article>) list, key);
        else if (list instanceof LinkedList)
            return exponentialSearchLinkedList((LinkedList<Article>) list, key);
        else
            throw new IllegalArgumentException("Unsupported List type");
    }
}