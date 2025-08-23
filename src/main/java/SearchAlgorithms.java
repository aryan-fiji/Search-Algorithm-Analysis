import java.util.*;

public class SearchAlgorithms {
    //article specific search methods
    public static int linearSearch(List<Article> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(key)) return i;
        }
        return -1;
    }
    
    public static int binarySearch(List<Article> list, String key) {
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
    
    public static int jumpSearch(List<Article> list, String key) {
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
    
    public static int exponentialSearch(List<Article> list, String key) {
        int n = list.size();
        if (n == 0) return -1;
        
        if (list.get(0).getId().equals(key)) return 0;
        
        int i = 1;
        while (i < n && list.get(i).getId().compareTo(key) <= 0) {
            i *= 2;
        }
        
        int result = binarySearch(list.subList(i / 2, Math.min(i, n)), key);
        return result != -1 ? result + (i / 2) : -1;
    }
}
