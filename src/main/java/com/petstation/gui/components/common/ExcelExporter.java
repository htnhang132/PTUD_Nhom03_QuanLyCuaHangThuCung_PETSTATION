package com.petstation.gui.components.common;

import com.petstation.gui.components.Dialogs;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * Utility để xuất dữ liệu từ JTable ra file Excel (CSV format) Support xuất cả từ TableModel hoặc
 * từ data 2D array
 */
public class ExcelExporter {

    /** Xuất JTable ra file Excel (CSV) */
    public static void exportTable(JComponent parent, JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất Excel");

        // Tạo tên file mặc định với timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileChooser.setSelectedFile(new File(defaultFileName + "_" + timestamp + ".csv"));

        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileFilter() {
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

            // Thêm .csv nếu chưa có
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try {
                exportTableToFile(table, file);
                Dialogs.showMessageDialog(
                        parent,
                        "Xuất file thành công!\nĐường dẫn: " + file.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                Dialogs.showMessageDialog(
                        parent,
                        "Lỗi khi xuất file: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Xuất table ra file cụ thể */
    private static void exportTableToFile(JTable table, File file) throws IOException {
        TableModel model = table.getModel();

        try (BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {

            // Write UTF-8 BOM để Excel nhận diện đúng tiếng Việt
            writer.write('\ufeff');

            // Write header
            for (int i = 0; i < model.getColumnCount(); i++) {
                if (i > 0) writer.write(",");
                writer.write(escapeCSV(model.getColumnName(i)));
            }
            writer.newLine();

            // Write data
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    if (col > 0) writer.write(",");

                    Object value = model.getValueAt(row, col);
                    String strValue = value == null ? "" : value.toString();
                    writer.write(escapeCSV(strValue));
                }
                writer.newLine();
            }
        }
    }

    /** Escape giá trị cho CSV format */
    private static String escapeCSV(String value) {
        if (value == null) return "";

        // Nếu có dấu phẩy, dấu ngoặc kép, hoặc xuống dòng -> wrap trong quotes
        if (value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r")) {
            value = value.replace("\"", "\"\""); // Escape quotes
            return "\"" + value + "\"";
        }

        return value;
    }

    /** Xuất data 2D array ra Excel */
    public static void exportData(
            JComponent parent, String[] headers, Object[][] data, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất Excel");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileChooser.setSelectedFile(new File(defaultFileName + "_" + timestamp + ".csv"));

        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileFilter() {
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
                exportDataToFile(headers, data, file);
                Dialogs.showMessageDialog(
                        parent,
                        "Xuất file thành công!\nĐường dẫn: " + file.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                Dialogs.showMessageDialog(
                        parent,
                        "Lỗi khi xuất file: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void exportDataToFile(String[] headers, Object[][] data, File file)
            throws IOException {
        try (BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {

            writer.write('\ufeff'); // UTF-8 BOM

            // Write headers
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) writer.write(",");
                writer.write(escapeCSV(headers[i]));
            }
            writer.newLine();

            // Write data
            for (Object[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) writer.write(",");
                    String value = row[i] == null ? "" : row[i].toString();
                    writer.write(escapeCSV(value));
                }
                writer.newLine();
            }
        }
    }

    /** Tạo nút "Xuất Excel"chuẩn */
    public static JButton createExportButton(JComponent parent, JTable table, String fileName) {
        JButton button = new JButton("Xuất Excel");
        button.setFocusPainted(false);
        button.addActionListener(e -> exportTable(parent, table, fileName));
        return button;
    }

    /** Tạo nút "Xuất Excel"với custom tooltip */
    public static JButton createExportButton(
            JComponent parent, JTable table, String fileName, String tooltip) {
        JButton button = createExportButton(parent, table, fileName);
        button.setToolTipText(tooltip);
        return button;
    }
}
