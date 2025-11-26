import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AppGUI {
    private JFrame frame;
    private JTextArea resultArea;
    private JComboBox<String> columnSelector;
    private JTable dataPreviewTable;
    private File currentFile;
    private List<String[]> previewData;
    private JButton analyzeButton; // Make analyze button a class field

    public void createAndShowGUI() {
        frame = new JFrame("Sorting Algorithm Performance Evaluator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for file upload and column selection
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for data preview
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for results
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton uploadButton = new JButton("Upload CSV File");
        columnSelector = new JComboBox<>();
        columnSelector.setEnabled(false);
        analyzeButton = new JButton("Analyze Performance"); // Initialize class field
        analyzeButton.setEnabled(false);

        uploadButton.addActionListener(e -> handleFileUpload());
        columnSelector.addActionListener(e -> handleColumnSelection());
        analyzeButton.addActionListener(e -> handleAnalysis());

        panel.add(uploadButton);
        panel.add(new JLabel("Select Column: "));
        panel.add(columnSelector);
        panel.add(analyzeButton);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Data Preview"));
        
        dataPreviewTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataPreviewTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Results"));
        
        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                previewData = CsvHandler.loadPreviewData(currentFile, 5); // Load first 5 rows for preview
                updateColumnSelector(previewData.get(0));
                updateDataPreview(previewData);
                analyzeButton.setEnabled(true); // Enable analyze button after file upload
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage());
            }
        }
    }

    private void updateColumnSelector(String[] headers) {
        columnSelector.removeAllItems();
        for (int i = 0; i < headers.length; i++) {
            columnSelector.addItem(headers[i] + " (Column " + i + ")");
        }
        columnSelector.setEnabled(true);
    }

    private void updateDataPreview(List<String[]> data) {
        if (data.isEmpty()) return;
        
        String[] headers = data.get(0);
        String[][] previewRows = data.subList(1, data.size()).toArray(new String[0][]);
        
        DefaultTableModel model = new DefaultTableModel(previewRows, headers);
        dataPreviewTable.setModel(model);
    }

    private void handleColumnSelection() {
        // Enable/disable analyze button based on column selection
        analyzeButton.setEnabled(columnSelector.getSelectedIndex() != -1);
    }

    private void handleAnalysis() {
        try {
            int selectedColumn = columnSelector.getSelectedIndex();
            List<Integer> data = CsvHandler.loadCsvColumn(currentFile, selectedColumn);
            
            if (data.isEmpty()) {
                throw new IllegalStateException("No valid numeric data found in the selected column");
            }

            int[] array = data.stream().mapToInt(Integer::intValue).toArray();
            Map<String, Long> results = new TreeMap<>();
            
            // Test all sorting algorithms
            results.put("Insertion Sort", PerformanceEvaluator.evaluateSortingPerformance(array, "Insertion Sort"));
            results.put("Shell Sort", PerformanceEvaluator.evaluateSortingPerformance(array, "Shell Sort"));
            results.put("Merge Sort", PerformanceEvaluator.evaluateSortingPerformance(array, "Merge Sort"));
            results.put("Quick Sort", PerformanceEvaluator.evaluateSortingPerformance(array, "Quick Sort"));
            results.put("Heap Sort", PerformanceEvaluator.evaluateSortingPerformance(array, "Heap Sort"));

            // Find the best performing algorithm
            Map.Entry<String, Long> bestAlgorithm = results.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .get();

            // Display results
            StringBuilder output = new StringBuilder();
            output.append(String.format("Array size: %,d elements%n%n", array.length));
            
            results.forEach((algorithm, time) -> {
                output.append(String.format("%s: %,d ns%n", algorithm, time));
            });
            
            output.append(String.format("%nBest performing algorithm: %s (%,d ns)", 
                bestAlgorithm.getKey(), bestAlgorithm.getValue()));

            resultArea.setText(output.toString());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }
}