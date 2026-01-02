package org.fin.walley.web;

import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.Subcategory;
import org.fin.walley.domain.Transaction;
import org.fin.walley.repo.AppUserRepository;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.fin.walley.repo.TransactionRepository;
import org.fin.walley.service.ImportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/export")
public class AdminExportController {

    private final AppUserRepository userRepo;
    private final TransactionRepository txRepo;
    private final CategoryRepository catRepo;
    private final SubcategoryRepository subRepo;
    private final ImportExportService importExportService;
    private final ObjectMapper objectMapper;

    public AdminExportController(AppUserRepository userRepo,
                                 TransactionRepository txRepo,
                                 CategoryRepository catRepo,
                                 SubcategoryRepository subRepo,
                                 ImportExportService importExportService,
                                 ObjectMapper objectMapper) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.catRepo = catRepo;
        this.subRepo = subRepo;
        this.importExportService = importExportService;
        this.objectMapper = objectMapper;
    }


    @GetMapping(value = "/all-transactions.csv", produces = "text/csv")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> exportAllTransactionsCsv() {
        List<ImportExportService.AdminTxRow> rows = txRepo.findAllForAdminExport().stream()
                .map(t -> new ImportExportService.AdminTxRow(
                        t.getUser().getUsername(),
                        t.getType(),
                        t.getAmount(),
                        t.getDate(),
                        t.getCategory() != null ? t.getCategory().getName() : null,
                        t.getSubcategory() != null ? t.getSubcategory().getName() : null,
                        t.getNote()
                ))
                .toList();

        String csv = importExportService.exportAllUsersTransactionsToCsv(rows);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        String filename = "all-transactions-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(bytes);
    }

    @GetMapping(value = "/all-transactions.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> exportAllTransactionsJson() {
        List<ImportExportService.AdminTxRow> rows = txRepo.findAllForAdminExport().stream()
                .map(t -> new ImportExportService.AdminTxRow(
                        t.getUser().getUsername(),
                        t.getType(),
                        t.getAmount(),
                        t.getDate(),
                        t.getCategory() != null ? t.getCategory().getName() : null,
                        t.getSubcategory() != null ? t.getSubcategory().getName() : null,
                        t.getNote()
                ))
                .toList();

        try {
            byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(rows);
            String filename = "all-transactions-" + LocalDate.now() + ".json";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bytes);
        } catch (Exception e) {
            throw new IllegalStateException("JSON export failed: " + e.getMessage(), e);
        }
    }


    @PostMapping(value = "/import/all-transactions/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<String> importAllTransactionsCsv(@RequestParam("file") MultipartFile file) {
        List<ImportExportService.AdminTxRow> rows = importExportService.importAllUsersTransactionsFromCsv(file);
        int imported = importRows(rows);
        return ResponseEntity.ok("Imported: " + imported);
    }

    @PostMapping(value = "/import/all-transactions/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<String> importAllTransactionsJson(@RequestParam("file") MultipartFile file) {
        try {
            List<ImportExportService.AdminTxRow> rows =
                    objectMapper.readValue(file.getInputStream(), new TypeReference<List<ImportExportService.AdminTxRow>>() {});
            int imported = importRows(rows);
            return ResponseEntity.ok("Imported: " + imported);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }


    private int importRows(List<ImportExportService.AdminTxRow> rows) {
        int count = 0;

        for (ImportExportService.AdminTxRow r : rows) {
            AppUser user = userRepo.findByUsername(r.username())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + r.username()));

            Category cat = catRepo.findByUserUsernameAndTypeAndName(user.getUsername(), r.type(), r.category())
                    .orElseGet(() -> catRepo.save(Category.builder()
                            .name(r.category())
                            .type(r.type())
                            .user(user)
                            .build()));

            Subcategory sub = null;
            if (r.subcategory() != null && !r.subcategory().isBlank()) {
                sub = subRepo.findByCategoryIdAndNameAndCategoryUserUsername(cat.getId(), r.subcategory(), user.getUsername())
                        .orElseGet(() -> subRepo.save(Subcategory.builder()
                                .name(r.subcategory())
                                .category(cat)
                                .build()));
            }

            Transaction tx = new Transaction();
            tx.setId(null);
            tx.setUser(user);
            tx.setType(r.type());
            tx.setAmount(r.amount());
            tx.setDate(r.date());
            tx.setCategory(cat);
            tx.setSubcategory(sub);
            tx.setNote(r.note());

            txRepo.save(tx);
            count++;
        }

        return count;
    }
}
