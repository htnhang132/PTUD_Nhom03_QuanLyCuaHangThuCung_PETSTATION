package com.petstation.gui.components.common;

import com.petstation.gui.components.Dialogs;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility để nhập dữ liệu từ file Excel (CSV format)
 * Hỗ trợ validate và xử lý lỗi
 */
public class ExcelImporter {

    /**
     * Kết quả import
     */
    public static class ImportResult {
        public String[] headers;
        public List<String[]> rows;
        public int totalRows;
        public List<String> errors;

        public ImportResult() {
            rows = new ArrayList<>();
            errors = new ArrayList<>();
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean isSuccess() {
            return errors.isEmpty() && totalRows > 0;
        }
    }

    /**
     * Import từ file CSV
     */
    public static ImportResult importFromCSV(JComponent parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || 
                       f.getName().toLowerCase().endsWith(".csv") ||
                       f.getName().toLowerCase().endsWith(".xls") ||
                       f.getName().toLowerCase().endsWith(".xlsx");
            }
            
            @Override
            public String getDescription() {
                return "Excel Files (*.csv, *.xls, *.xlsx)";
            }
        });

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                return parseCSVFile(file);
            } catch (IOException e) {
                ImportResult errorResult = new ImportResult();
                errorResult.errors.add("Lỗi đọc file: "+ e.getMessage());
                return errorResult;
            }
        }
        
        return null; // User cancelled
    }

    /**
     * Parse file CSV
     */
    private static ImportResult parseCSVFile(File file) throws IOException {
        ImportResult result = new ImportResult();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            
            // Đọc header (dòng đầu tiên)
            String headerLine = reader.readLine();
            if (headerLine == null) {
                result.errors.add("File rỗng hoặc không đúng định dạng");
                return result;
            }
            
            // Skip UTF-8 BOM nếu có
            if (headerLine.startsWith("\ufeff")) {
                headerLine = headerLine.substring(1);
            }
            
            result.headers = parseCSVLine(headerLine);
            
            // Đọc các dòng dữ liệu
            String line;
            int lineNumber = 2; // Bắt đầu từ 2 vì header là dòng 1
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                String[] values = parseCSVLine(line);
                
                // Validate số cột
                if (values.length != result.headers.length) {
                    result.errors.add(String.format(
                        "Dòng %d: Số cột không khớp (có %d, cần %d)",
                        lineNumber, values.length, result.headers.length));
                } else {
                    result.rows.add(values);
                }
                
                lineNumber++;
            }
            
            result.totalRows = result.rows.size();
            
            if (result.totalRows == 0 && result.errors.isEmpty()) {
                result.errors.add("File không có dữ liệu");
            }
        }
        
        return result;
    }

    /**
     * Parse một dòng CSV với xử lý quotes và commas
     */
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote ("")
                    currentValue.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // End of value
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add last value
        values.add(currentValue.toString().trim());
        
        return values.toArray(new String[0]);
    }

    /**
     * Hiển thị dialog kết quả import
     */
    public static void showImportResult(JComponent parent, ImportResult result) {
        if (result == null) {
            return; // User cancelled
        }
        
        if (result.isSuccess()) {
            Dialogs.showMessageDialog(parent,
                String.format("Nhập thành công %d dòng dữ liệu!", result.totalRows),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (result.hasErrors()) {
            StringBuilder message = new StringBuilder();
            message.append("Có lỗi khi nhập dữ liệu:\n\n");
            
            int maxErrors = Math.min(5, result.errors.size());
            for (int i = 0; i < maxErrors; i++) {
                message.append("• ").append(result.errors.get(i)).append("\n");
            }
            
            if (result.errors.size() > 5) {
                message.append(String.format("\n... và %d lỗi khác", result.errors.size() - 5));
            }
            
            if (result.totalRows > 0) {
                message.append(String.format("\n\nĐã nhập thành công: %d dòng", result.totalRows));
            }
            
            Dialogs.showMessageDialog(parent,
                message.toString(),
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Tạo nút "Nhập Excel"chuẩn
     */
    public static JButton createImportButton(JComponent parent, ImportCallback callback) {
        JButton button = new JButton("Nhập Excel");
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            ImportResult result = importFromCSV(parent);
            if (result != null) {
                callback.onImport(result);
                showImportResult(parent, result);
            }
        });
        return button;
    }

    /**
     * Callback interface cho xử lý dữ liệu import
     */
    public interface ImportCallback {
        void onImport(ImportResult result);
    }

    /**
     * Tải template Excel mẫu
     */
    public static void downloadTemplate(JComponent parent, String[] headers, String fileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file mẫu");
        fileChooser.setSelectedFile(new File(fileName + "_template.csv"));
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try {
                createTemplateFile(headers, file);
                Dialogs.showMessageDialog(parent,
                    "Tải file mẫu thành công!\nĐường dẫn: "+ file.getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                Dialogs.showMessageDialog(parent,
                    "Lỗi khi tạo file mẫu: "+ e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void createTemplateFile(String[] headers, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            
            writer.write('\ufeff'); // UTF-8 BOM
            
            // Write headers
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) writer.write(",");
                writer.write(headers[i]);
            }
            writer.newLine();
            
            // Write sample row (empty values)
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) writer.write(",");
                writer.write(""); // Empty sample
            }
            writer.newLine();
        }
    }
}
