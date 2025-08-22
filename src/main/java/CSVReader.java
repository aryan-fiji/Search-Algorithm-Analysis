import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVReader {
    
    //Reads CSV file and returns a list of Article objects
    public static List<Article> readCSV(String filePath) {
        List<Article> articles = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            int successfulReads = 0;
            int errors = 0;
            StringBuilder multiLineRecord = new StringBuilder();
            boolean inMultiLineRecord = false;
            int recordStartLine = 0;
            
            System.out.println("Reading CSV file: " + filePath);
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    System.out.println("CSV Header: " + line.substring(0, Math.min(100, line.length())) + "...");
                    continue;
                }
                
                // Handle records that span multiple lines
                if (!inMultiLineRecord) {
                    multiLineRecord = new StringBuilder(line);
                    recordStartLine = lineNumber;
                    inMultiLineRecord = true;
                } 
                
                else {
                    multiLineRecord.append(" ").append(line);
                }
                
                // Check if we have a complete record (9 properly separated fields)
                String currentRecord = multiLineRecord.toString();
                List<String> fields = parseCSVFields(currentRecord);
                
                if (fields.size() >= 9) {
                    try {
                        Article article = parseCSVLine(currentRecord, recordStartLine);
                        if (article != null) {
                            articles.add(article);
                            successfulReads++;
                            
                            //Show progress for csv data compilation
                            if (successfulReads % 1000 == 0) {
                                System.out.println("Processed " + successfulReads + " articles...");
                            }
                        }

                    } catch (Exception e) {
                        errors++;
                        if (errors <= 10) { 
                            System.err.println("Error parsing record starting at line " + recordStartLine + ": " + e.getMessage());
                        }
                    }
                    
                    // Reset for next record
                    inMultiLineRecord = false;
                    multiLineRecord = new StringBuilder();
                }
            }
            
            // Handle any remaining incomplete record at end of file
            if (inMultiLineRecord && multiLineRecord.length() > 0) {
                try {
                    String currentRecord = multiLineRecord.toString();
                    List<String> fields = parseCSVFields(currentRecord);
                    if (fields.size() >= 9) {
                        Article article = parseCSVLine(currentRecord, recordStartLine);
                        if (article != null) {
                            articles.add(article);
                            successfulReads++;
                        }
                    }
                } catch (Exception e) {
                    errors++;
                }
            }
            
            System.out.println("\nCSV Reading Summary:");
            System.out.println("Successfully loaded articles: " + successfulReads);
            System.out.println("Errors encountered: " + errors);
            
        } 
        
        catch (FileNotFoundException e) {
            System.err.println("Error: CSV file not found at path: " + filePath);
            System.err.println("Please check the file path and ensure the file exists.");
        } 
        
        catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return articles;
    }
    
    //Parses a single CSV line into an Article object
    private static Article parseCSVLine(String line, int lineNumber) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        List<String> fields = parseCSVFields(line);
        
        //Expected header fields from csv file
        if (fields.size() < 9) {
            throw new IllegalArgumentException("Insufficient fields. Expected 9, got " + fields.size());
        }
        
        try {
            String id = cleanField(fields.get(0));
            String title = cleanField(fields.get(1));
            String abstractText = cleanField(fields.get(2));
            
            // Parse integer fields, defaulting to 0 if parsing fails
            int computerScience = parseIntegerField(fields.get(3), "Computer Science");
            int physics = parseIntegerField(fields.get(4), "Physics");
            int mathematics = parseIntegerField(fields.get(5), "Mathematics");
            int statistics = parseIntegerField(fields.get(6), "Statistics");
            int quantitativeBiology = parseIntegerField(fields.get(7), "Quantitative Biology");
            int quantitativeFinance = parseIntegerField(fields.get(8), "Quantitative Finance");
            
            // Validate required fields
            if (id.isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            
            return new Article(id, title, abstractText, computerScience, physics, 
                mathematics, statistics, quantitativeBiology, quantitativeFinance);
                             
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Article object: " + e.getMessage());
        }
    }
    
    //Parses CSV fields, handling quoted fields and commas within quotes
    private static List<String> parseCSVFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        // Parse character by character to handle quotes and commas correctly
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes) {

                    // Check for escaped quote (double quote "")
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        currentField.append('"'); 
                        i++; 
                    } 
                    else {
                        // End of quoted field
                        inQuotes = false;
                    }
                }   else {
                        // Start of quoted field
                        inQuotes = true;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator found outside quotes
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                // Regular character or comma inside quotes
                currentField.append(c);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString().trim());

        return fields;
    }
    
    //Cleans a field by removing surrounding quotes and handling whitespace
    private static String cleanField(String field) {
        if (field == null) return "";
        
        field = field.trim();
        
        // Remove surrounding quotes if present
        if (field.length() >= 2 && field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
            // Replace escaped quotes within the field ("" becomes ")
            field = field.replace("\"\"", "\"");
        }
        
        // Clean up any remaining whitespace issues from multiline records
        field = field.replaceAll("\\s+", " ").trim();
        
        return field;
    }
    
    //Parses an integer field, handling various formats and edge cases
    private static int parseIntegerField(String field, String fieldName) {
        if (field == null || field.trim().isEmpty()) {
            return 0;
        }
        
        field = cleanField(field);
        
        try {
            // Handle decimal values by truncating to integer
            if (field.contains(".")) {
                double doubleValue = Double.parseDouble(field);
                return (int) doubleValue;
            }
            return Integer.parseInt(field);
        } 
        
        catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse " + fieldName + " value '" + field + "', defaulting to 0");
            return 0;
        }
    }
    
    //Validates the CSV file format by checking the header
    public static boolean validateCSVFormat(String filePath) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("Error: CSV file is empty");
                return false;
            }
            
            // Check if header contains expected columns (case-insensitive)
            String lowerHeader = headerLine.toLowerCase();
            String[] expectedColumns = {"id", "title", "abstract", "computer science", "physics", 
                                      "mathematics", "statistics", "quantitative biology", "quantitative finance"};
            
            int foundColumns = 0;
            for (String expectedColumn : expectedColumns) {
                if (lowerHeader.contains(expectedColumn)) {
                    foundColumns++;
                }
            }
            
            if (foundColumns >= 6) { 
                System.out.println("CSV format validation passed (" + foundColumns + "/9 expected columns found)");
                return true;
            } else {
                System.err.println("CSV formatting failed. Expected header columns not found");
                System.err.println("Header: " + headerLine);
                return false;
            }
            
        } catch (IOException e) {
            System.err.println("Error validating CSV format: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets basic statistics about the CSV file
     * @param filePath Path to the CSV file
     */
    public static void printCSVStats(String filePath) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            
            int lineCount = 0;
            long fileSize = new File(filePath).length();
            
            while ((br.readLine()) != null) {
                lineCount++;
            }
            
            System.out.println("\nCSV File Statistics:");
            System.out.println("File path: " + filePath);
            System.out.println("File size: " + formatFileSize(fileSize));
            System.out.println("Total lines: " + lineCount);
            System.out.println("Data rows (excluding header): " + (lineCount - 1));
            
        } catch (IOException e) {
            System.err.println("Error reading CSV statistics: " + e.getMessage());
        }
    }
    
    //Formats file size in human-readable format
    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int unit = 1000;
        int exp = (int) (Math.log(size) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
    }
}