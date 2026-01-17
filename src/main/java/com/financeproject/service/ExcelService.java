package com.financeproject.service;

import com.financeproject.entity.Transaction;
import com.financeproject.repository.ITransactionRepository;
import com.financeproject.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service class for generating Excel reports.
 * Uses the Apache POI library to create .xlsx files.
 */
@Service
public class ExcelService {

    private final ITransactionRepository transactionRepository;

    public ExcelService(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Generates an Excel file containing the user's transaction history.
     * @param user The user requesting the report
     * @return ByteArrayInputStream containing the Excel file data
     * @throws IOException If file creation fails
     */
    public ByteArrayInputStream exportUserTransactions(User user) throws IOException {
        // Fetch all transactions sorted by date
        List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);

        // Try-with-resources ensures the Workbook is closed automatically (Memory Management)
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Create a new sheet named "Expenses"
            Sheet sheet = workbook.createSheet("Expenses");

            // --- HEADER ROW SETUP ---
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Date", "Description", "Category", "Type", "Amount"};
            
            // Define Header Style (Bold Font + Grey Background)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create Header Cells
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- FILL DATA ROWS ---
            int rowIdx = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowIdx++);

                // Column 0: Date
                row.createCell(0).setCellValue(t.getTransactionDate().toString());
                
                // Column 1: Description
                row.createCell(1).setCellValue(t.getDescription());
                
                // Column 2: Category (Handle null safety)
                row.createCell(2).setCellValue(t.getCategory() != null ? t.getCategory().getName() : "-");
                
                // Column 3: Type (INCOME / EXPENSE)
                row.createCell(3).setCellValue(t.getType().name()); 
                
                // Column 4: Amount
                row.createCell(4).setCellValue(t.getAmount().doubleValue());
            }

            // --- AUTO-SIZE COLUMNS ---
            // Adjusts column width to fit the content automatically for better readability
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the workbook to the output stream
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}