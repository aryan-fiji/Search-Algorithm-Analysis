import java.util.*;

public class SearchAlgorithms {
    public static <T> int linearSearchArrayList(ArrayList<T> list, T key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(key)) return i;
        }
        return -1;
    }

    public static <T> int linearSearchLinkedList(LinkedList<T> list, T key) {
        int index = 0;
        for (T item : list) {
            if (item.equals(key)) return index;
            index++;
        }
        return -1;
    }

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

    public static <T extends Comparable<T>> int binarySearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return binarySearchArrayList(tempList, key);
    }

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

    public static <T extends Comparable<T>> int jumpSearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return jumpSearchArrayList(tempList, key);
    }

    public static <T extends Comparable<T>> int exponentialSearchArrayList(ArrayList<T> list, T key) {
        int n = list.size();
        if (list.get(0).compareTo(key) == 0) return 0;
        int i = 1;
        while (i < n && list.get(i).compareTo(key) <= 0) {
            i *= 2;
        }
        return binarySearchArrayList(new ArrayList<>(list.subList(i / 2, Math.min(i, n))), key);
    }

    public static <T extends Comparable<T>> int exponentialSearchLinkedList(LinkedList<T> list, T key) {
        ArrayList<T> tempList = new ArrayList<>(list);
        return exponentialSearchArrayList(tempList, key);
    }

    public static Article searchById(List<Article> list, String id) {
        for (Article a : list) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }
}