 import java.util.*;

// implement algorithm using arraylist and linkedlist
class Article implements Comparable<Article> {
    private String title;
    private String author;

    public Article(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    @Override
    public int compareTo(Article other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return "Article{" + "title='" + title + "', author='" + author + "'}";
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

    //jumpp search (array)
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

    //jumpp search (linkedlist)
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

    // ---------- TEST ----------
    public static void main(String[] args) {
        ArrayList<Article> arrayList = new ArrayList<>();
        LinkedList<Article> linkedList = new LinkedList<>();

        //sample data
        arrayList.add(new Article("Alpha", "John"));
        arrayList.add(new Article("Beta", "Jane"));
        arrayList.add(new Article("Gamma", "Jim"));
        Collections.sort(arrayList);

        linkedList.addAll(arrayList);
        Article searchKey = new Article("Beta", "Jane");

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