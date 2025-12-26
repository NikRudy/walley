package org.fin.walley.web;

import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Transaction;
import org.fin.walley.repo.AppUserRepository;
import org.fin.walley.repo.TransactionRepository;
import org.fin.walley.service.ImportExportService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/export")
public class AdminExportController {

    private final AppUserRepository userRepo;
    private final TransactionRepository txRepo;
    private final ImportExportService importExportService;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    public AdminExportController(AppUserRepository userRepo,
                                 TransactionRepository txRepo,
                                 ImportExportService importExportService,
                                 ObjectMapper objectMapper) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.importExportService = importExportService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/users-tasks.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportUsersTasksCsv() {
        List<ImportExportService.UserWithTasksJson> payload = buildPayload();

        String csv = importExportService.exportUsersWithTasksToCsv(payload);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users-tasks.csv\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(bytes);
    }

    @GetMapping(value = "/users-tasks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportUsersTasksJson() {
        List<ImportExportService.UserWithTasksJson> payload = buildPayload();

        try {
            byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(payload);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users-tasks.json\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bytes);
        } catch (Exception e) {
            throw new IllegalStateException("JSON export failed: " + e.getMessage(), e);
        }
    }

    private List<ImportExportService.UserWithTasksJson> buildPayload() {
        // все пользователи
        List<AppUser> users = userRepo.findAll(Sort.by(Sort.Direction.ASC, "username"));

        // все транзакции с join fetch (user/category/subcategory)
        List<Transaction> allTx = txRepo.findAllForAdminExport();

        Map<Long, List<Transaction>> byUserId = allTx.stream()
                .filter(t -> t.getUser() != null && t.getUser().getId() != null)
                .collect(Collectors.groupingBy(t -> t.getUser().getId()));

        List<ImportExportService.UserWithTasksJson> payload = new ArrayList<>();

        for (AppUser u : users) {
            List<Transaction> txList = byUserId.getOrDefault(u.getId(), List.of());

            List<ImportExportService.AdminTxJson> tasks = txList.stream()
                    .map(t -> new ImportExportService.AdminTxJson(
                            t.getId(),
                            t.getType(),
                            t.getAmount(),
                            t.getDate(),
                            t.getCategory() != null ? t.getCategory().getName() : null,
                            t.getSubcategory() != null ? t.getSubcategory().getName() : null,
                            t.getNote()
                    ))
                    .toList();

            payload.add(new ImportExportService.UserWithTasksJson(
                    u.getId(),
                    u.getUsername(),
                    u.getRole() != null ? u.getRole().name() : null,
                    u.isEnabled(),
                    tasks
            ));
        }

        return payload;
    }
}
