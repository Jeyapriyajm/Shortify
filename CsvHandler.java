// CsvHandler.java
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler {
    public static List<String[]> loadPreviewData(File file, int numRows) throws IOException {
        List<String[]> previewData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < numRows) {
                previewData.add(line.split(","));
                count++;
            }
        }
        return previewData;
    }

    public static List<Integer> loadCsvColumn(File file, int columnIndex) throws IOException {
        List<Integer> columnData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    if (columnIndex < values.length) {
                        columnData.add(Integer.parseInt(values[columnIndex].trim()));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid number: " + e.getMessage());
                }
            }
        }
        return columnData;
    }
}