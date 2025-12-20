package org.fin.walley.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportExportService {

    // CSV header: type,amount,date,category,subcategory,note

    public record CsvRow(
            TransactionType type,
            BigDecimal amount,
            LocalDate date,
            String category,
            String subcategory,
            String note
    ) {}

    public String exportTransactionsToCsv(List<Transaction> txList) {
        StringWriter out = new StringWriter();
        try (CSVWriter writer = new CSVWriter(out)) {
            writer.writeNext(new String[]{"type", "amount", "date", "category", "subcategory", "note"});

            for (Transaction tx : txList) {
                String cat = tx.getCategory() != null ? tx.getCategory().getName() : "";
                String sub = tx.getSubcategory() != null ? tx.getSubcategory().getName() : "";

                writer.writeNext(new String[]{
                        tx.getType().name(),
                        tx.getAmount().toPlainString(),
                        tx.getDate().toString(),
                        cat,
                        sub,
                        nullToEmpty(tx.getNote())
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public List<CsvRow> importRowsFromCsv(MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVReader csv = new CSVReader(reader)) {

            List<String[]> rows = csv.readAll();
            if (rows.isEmpty()) return List.of();

            List<CsvRow> result = new ArrayList<>();

            // skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] r = rows.get(i);
                if (r.length < 4) continue;

                TransactionType type = TransactionType.valueOf(r[0].trim());
                BigDecimal amount = new BigDecimal(r[1].trim());
                LocalDate date = LocalDate.parse(r[2].trim());
                String category = safe(r, 3);        // required
                String subcategory = safe(r, 4);     // optional
                String note = safe(r, 5);            // optional

                if (category == null || category.isBlank()) {
                    throw new IllegalArgumentException("CSV row has empty category (required)");
                }

                result.add(new CsvRow(type, amount, date, category, subcategory, note));
            }

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid CSV format: " + e.getMessage(), e);
        }
    }

    private static String safe(String[] row, int idx) {
        if (idx >= row.length) return null;
        String v = row[idx];
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
