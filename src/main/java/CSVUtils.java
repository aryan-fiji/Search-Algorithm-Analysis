import java.io.*;
import java.util.*;
import com.opencsv.*;
import java.nio.charset.StandardCharsets;

public class CSVUtils {
    public static List<Article> readCSV(String filename) {
        List<Article> articles = new ArrayList<>();
        try (
            InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(filename);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVReader csvReader = new CSVReader(reader)
        ) {
            String[] nextLine;
            boolean isFirstLine = true;
            while ((nextLine = csvReader.readNext()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                String id = nextLine.length > 0 ? nextLine[0].trim() : "";
                String title = nextLine.length > 1 ? nextLine[1].trim() : "";
                String abstractText = nextLine.length > 2 ? nextLine[2].trim() : "";
                int cs = safeParseInt(nextLine, 3);
                int phy = safeParseInt(nextLine, 4);
                int math = safeParseInt(nextLine, 5);
                int stat = safeParseInt(nextLine, 6);
                int qb = safeParseInt(nextLine, 7);
                int qf = safeParseInt(nextLine, 8);
                articles.add(new Article(id, title, abstractText, cs, phy, math, stat, qb, qf));
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return articles;
    }

    private static int safeParseInt(String[] arr, int idx) {
        if (arr.length > idx) {
            try {
                return Integer.parseInt(arr[idx].trim());
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
}