import java.util.Objects;

public class Article implements Comparable<Article> {
    private String id;
    private String title;
    private String abstractText;
    private int computerScience, physics, mathematics, statistics, quantitativeBiology, quantitativeFinance;

    // Constructors
    public Article(String id, String title, String abstractText,int computerScience, int physics, 
                    int mathematics, int statistics, int quantitativeBiology, int quantitativeFinance) {
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

    // Getters
    public String getId() { 
        return id; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getAbstractText() { 
        return abstractText; 
    }
    
    public int getComputerScience() { 
        return computerScience; 
    }
    
    public int getPhysics() { 
        return physics; 
    }
    
    public int getMathematics() { 
        return mathematics; 
    }
    
    public int getStatistics() { 
        return statistics; 
    }
    
    public int getQuantitativeBiology() { 
        return quantitativeBiology; 
    }
    
    public int getQuantitativeFinance() { 
        return quantitativeFinance; 
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }
    
    public void setComputerScience(int computerScience) {
        this.computerScience = computerScience;
    }
    
    public void setPhysics(int physics) {
        this.physics = physics;
    }
    
    public void setMathematics(int mathematics) {
        this.mathematics = mathematics;
    }
    
    public void setStatistics(int statistics) {
        this.statistics = statistics;
    }
    
    public void setQuantitativeBiology(int quantitativeBiology) {
        this.quantitativeBiology = quantitativeBiology;
    }
    
    public void setQuantitativeFinance(int quantitativeFinance) {
        this.quantitativeFinance = quantitativeFinance;
    }

    // Sort articles by their ID
    @Override
    public int compareTo(Article other) {
        if (other == null) return 1;
            return this.id.compareTo(other.id);
    }
    
    // Compare based on ID only for search operations
    public int compareTo(String otherId) {
        if (otherId == null) return 1;
            return this.id.compareTo(otherId);
    }

    // Two articles are equal if they have same ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;

        if (obj == null || getClass() != obj.getClass()) 
            return false;
            
        Article article = (Article) obj;
        return Objects.equals(id, article.id);
    }

    // Hash based on ID for consistency
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // debug representation for strings
    @Override
    public String toString() {
        return "ID: " + id + 
               "\nTitle: " + title + 
               "\nAbstract:\n" + abstractText +
               ", Physics: " + physics + 
               ", Math: " + mathematics + 
               ", Stats: " + statistics + 
               ", QuantBio: " + quantitativeBiology + 
               ", QuantFin: " + quantitativeFinance + "]";
    }

    // Checks if this article matches a given ID
    public boolean matchesId(String searchId) {
        return this.id.equals(searchId);
    }
    
    public boolean hasCategories() {
        return computerScience > 0 || physics > 0 || mathematics > 0 || 
               statistics > 0 || quantitativeBiology > 0 || quantitativeFinance > 0;
    }
    
    public int getCategoryCount() {
        return computerScience + physics + mathematics + statistics + quantitativeBiology + quantitativeFinance;
    }

    public String toShortString() {
        return "Article{id='" + id + "', title='" + 
               (title.length() > 30 ? title.substring(0, 30) + "..." : title) + "'}";
    }
    
    public Article copy() {
        return new Article(id, title, abstractText, computerScience, physics, 
                          mathematics, statistics, quantitativeBiology, quantitativeFinance);
    }
    
    public static Article createDummyArticle(String id) {
        return new Article(id, "", "", 0, 0, 0, 0, 0, 0);
    }
    
    // Compares articles
    public boolean deepEquals(Article other) {
        if (other == null) return false;
        return Objects.equals(id, other.id) &&
               Objects.equals(title, other.title) &&
               Objects.equals(abstractText, other.abstractText) &&
               computerScience == other.computerScience &&
               physics == other.physics &&
               mathematics == other.mathematics &&
               statistics == other.statistics &&
               quantitativeBiology == other.quantitativeBiology &&
               quantitativeFinance == other.quantitativeFinance;
    }
}