import java.util.Objects;

public class Article implements Comparable<Article> {
    private String id;
    private String title;
    private String abstractText;
    private int computerScience, physics, mathematics, statistics, quantitativeBiology, quantitativeFinance;

    public Article(String id, String title, String abstractText,
                   int computerScience, int physics, int mathematics, int statistics,
                   int quantitativeBiology, int quantitativeFinance) {
        this.id = id;
        this.title = title;
        this.abstractText = abstractText;
        this.computerScience = computerScience;
        this.physics = physics;
        this.mathematics = mathematics;
        this.statistics = statistics;
        this.quantitativeBiology = quantitativeBiology;
        this.quantitativeFinance = quantitativeFinance;
    }

    public String getId() { 
        return id; }
    public String getTitle() { 
        return title; }
    public String getAbstractText() { 
        return abstractText; }
    public int getComputerScience() { 
        return computerScience; }
    public int getPhysics() { 
        return physics; }
    public int getMathematics() { 
        return mathematics; }
    public int getStatistics() { 
        return statistics; }
    public int getQuantitativeBiology() { 
        return quantitativeBiology; }
    public int getQuantitativeFinance() { 
        return quantitativeFinance; }

    @Override
    public int compareTo(Article other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;

        if (obj == null || getClass() != obj.getClass()) 
            return false;
            
        Article article = (Article) obj;
            return Objects.equals(id, article.id) || Objects.equals(title, article.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nTitle: " + title + "\nAbstract: " + abstractText +
               "\nComputer Science: " + computerScience +
               "\nPhysics: " + physics +
               "\nMathematics: " + mathematics +
               "\nStatistics: " + statistics +
               "\nQuantitative Biology: " + quantitativeBiology +
               "\nQuantitative Finance: " + quantitativeFinance;
    }
}
