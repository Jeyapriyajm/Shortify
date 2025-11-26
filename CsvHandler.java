import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler {

    // Returns first numRows lines (including header) split by comma
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

    // Returns numeric values in the selected column (skips invalid values)
    public static List<Integer> loadCsvColumn(File file, int columnIndex) throws IOException {
        List<Integer> columnData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (columnIndex < values.length) {
                    try {
                        columnData.add(Integer.parseInt(values[columnIndex].trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid number: " + e.getMessage());
                    }
                }
            }
        }

        return columnData;
    }
}
