package org.fin.walley.api;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.Subcategory;
import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.fin.walley.service.ImportExportService;
import org.fin.walley.service.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/transactions")
public class TransactionApiController {


    private final TransactionService txService;
    private final ImportExportService importExport;
    private final CategoryRepository catRepo;
    private final SubcategoryRepository subRepo;


    public TransactionApiController(TransactionService txService,
                                    ImportExportService importExport,
                                    CategoryRepository catRepo,
                                    SubcategoryRepository subRepo) {
        this.txService = txService;
        this.importExport = importExport;
        this.catRepo = catRepo;
        this.subRepo = subRepo;
    }


    public record TxDto(
            Long id,
            TransactionType type,
            BigDecimal amount,
            LocalDate date,
            Long categoryId,
            String categoryName,
            Long subcategoryId,
            String subcategoryName,
            String note
    ) {
        static TxDto from(Transaction t) {
            Long catId = t.getCategory() != null ? t.getCategory().getId() : null;
            String catName = t.getCategory() != null ? t.getCategory().getName() : null;
            Long subId = t.getSubcategory() != null ? t.getSubcategory().getId() : null;
            String subName = t.getSubcategory() != null ? t.getSubcategory().getName() : null;
            return new TxDto(t.getId(), t.getType(), t.getAmount(), t.getDate(), catId, catName, subId, subName, t.getNote());
        }
    }


    public record TxUpsert(
            @NotNull TransactionType type,
            @NotNull @Positive @Digits(integer = 12, fraction = 2) BigDecimal amount,
            @NotNull LocalDate date,
            @NotNull Long categoryId,
            Long subcategoryId,
            @Size(max = 255) String note
    ) {
        Transaction toEntity() {
            Transaction t = new Transaction();
            t.setType(type);
            t.setAmount(amount);
            t.setDate(date);
            t.setNote(note);
            return t;
        }
    }
    @GetMapping
    public List<TxDto> list(Principal principal) {
        return txService.listForUser(principal.getName()).stream().map(TxDto::from).toList();
    }


    @PostMapping
    public TxDto create(@RequestBody @Valid TxUpsert body, Principal principal) {
        Transaction saved = txService.create(principal.getName(), body.toEntity(), body.categoryId(), body.subcategoryId());
        return TxDto.from(saved);
    }


    @PutMapping("/{id}")
    public TxDto update(@PathVariable Long id, @RequestBody @Valid TxUpsert body, Principal principal) {
        Transaction updated = txService.update(principal.getName(), id, body.toEntity(), body.categoryId(), body.subcategoryId());
        return TxDto.from(updated);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        txService.delete(principal.getName(), id);
    }


// --- Export/Import CSV ---


    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv(Principal principal) {
        String csv = importExport.exportTransactionsToCsv(txService.listForUser(principal.getName()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }


    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<TxDto> importCsv(@RequestParam("file") MultipartFile file, Principal principal) {
        String username = principal.getName();
        List<ImportExportService.CsvRow> rows = importExport.importRowsFromCsv(file);


        for (ImportExportService.CsvRow r : rows) {
// найдём/создадим category по имени (внутри пользователя)
            Category cat = catRepo.findByUserUsernameAndTypeAndName(username, r.type(), r.category())
                    .orElseGet(() -> catRepo.save(Category.builder()
                            .name(r.category())
                            .type(r.type())
                            .user(txService.requireUser(username))
                            .build()));


            Long subId = null;
            if (r.subcategory() != null && !r.subcategory().isBlank()) {
                Subcategory sub = subRepo.findByCategoryIdAndNameAndCategoryUserUsername(cat.getId(), r.subcategory(), username)
                        .orElseGet(() -> subRepo.save(Subcategory.builder()
                                .name(r.subcategory())
                                .category(cat)
                                .build()));
                subId = sub.getId();
            }


            Transaction t = new Transaction();
            t.setType(r.type());
            t.setAmount(r.amount());
            t.setDate(r.date());
            t.setNote(r.note());


            txService.create(username, t, cat.getId(), subId);
        }


        return list(principal);
    }


// --- Export/Import JSON ---


    @GetMapping(value = "/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TxDto> exportJson(Principal principal) {
        return list(principal);
    }


    @PostMapping(value = "/import/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<TxDto> importJson(@RequestBody List<@Valid TxUpsert> body, Principal principal) {
        String username = principal.getName();
        for (TxUpsert item : body) {
            txService.create(username, item.toEntity(), item.categoryId(), item.subcategoryId());
        }
        return list(principal);
    }
}