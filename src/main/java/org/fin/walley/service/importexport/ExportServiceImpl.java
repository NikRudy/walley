package org.fin.walley.service.importexport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.audit.ImportExportJob;
import org.fin.walley.domain.audit.JobFormat;
import org.fin.walley.domain.audit.JobStatus;
import org.fin.walley.domain.audit.JobType;
import org.fin.walley.domain.user.User;
import org.fin.walley.dto.finance.TransactionDto;
import org.fin.walley.dto.importexport.ExportRequestDto;
import org.fin.walley.repository.audit.ImportExportJobRepository;
import org.fin.walley.repository.user.UserRepository;
import org.fin.walley.service.finance.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса экспорта транзакций пользователя.
 * Выполняет выборку транзакций через TransactionService,
 * логирует операцию в ImportExportJob и формирует JSON/CSV.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExportServiceImpl implements ExportService {

    private final TransactionService transactionService;
    private final ImportExportJobRepository jobRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public byte[] exportTransactions(Long userId, ExportRequestDto requestDto) {
        // 1. Проверяем, что пользователь существует
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 2. Преобразуем LocalDate в границы LocalDateTime
        LocalDateTime from = toDateTimeStart(requestDto.getDateFrom());
        LocalDateTime to = toDateTimeEnd(requestDto.getDateTo());

        // 3. Получаем транзакции по заданным фильтрам
        List<TransactionDto> transactions = transactionService.getTransactions(
                userId,
                from,
                to,
                null,                           // тип транзакции (INCOME/EXPENSE) опционален
                requestDto.getAccountId(),
                null,                           // categoryId (фильтр можно добавить позже)
                null,                           // subcategoryId
                requestDto.isIncludeDeleted()
        );

        // 4. Логируем операцию экспорта в ImportExportJob
        ImportExportJob job = new ImportExportJob();
        job.setUser(user);
        job.setType(JobType.EXPORT);
        job.setFormat(requestDto.getFormat());
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setStartedAt(LocalDateTime.now());

        job = jobRepository.save(job);

        try {
            // 5. Формируем файл в нужном формате
            byte[] data;
            if (requestDto.getFormat() == JobFormat.JSON) {
                data = objectMapper.writeValueAsBytes(transactions);
            } else {
                // CSV экспорт: простая реализация на основе заголовка и строк
                String csv = buildCsv(transactions);
                data = csv.getBytes(StandardCharsets.UTF_8);
            }

            // 6. Обновляем статус задачи как успешный
            job.setStatus(JobStatus.SUCCESS);
            job.setFinishedAt(LocalDateTime.now());
            jobRepository.save(job);

            return data;
        } catch (Exception ex) {
            // 7. В случае ошибки помечаем задачу как FAILED и сохраняем текст ошибки
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(ex.getMessage());
            job.setFinishedAt(LocalDateTime.now());
            jobRepository.save(job);

            throw new IllegalStateException("Failed to export transactions", ex);
        }
    }

    @Override
    public String buildExportFileName(Long userId, ExportRequestDto requestDto) {
        StringBuilder sb = new StringBuilder("walley-transactions-");
        sb.append(userId);

        if (requestDto.getDateFrom() != null || requestDto.getDateTo() != null) {
            sb.append("-");
            sb.append(requestDto.getDateFrom() != null ? requestDto.getDateFrom() : "");
            sb.append("_");
            sb.append(requestDto.getDateTo() != null ? requestDto.getDateTo() : "");
        }

        String extension = requestDto.getFormat() == JobFormat.JSON ? ".json" : ".csv";
        sb.append(extension);

        return sb.toString();
    }

    // ----------------- Вспомогательные методы -----------------

    private LocalDateTime toDateTimeStart(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    private LocalDateTime toDateTimeEnd(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }

    private String buildCsv(List<TransactionDto> transactions) {
        String header = "id,userId,accountId,categoryId,subcategoryId,amount,type,occurredAt,description,deleted";

        String rows = transactions.stream()
                .map(t -> String.join(",",
                        safeString(t.getId()),
                        safeString(t.getUserId()),
                        safeString(t.getAccountId()),
                        safeString(t.getCategoryId()),
                        safeString(t.getSubcategoryId()),
                        safeString(t.getAmount()),
                        t.getType() != null ? t.getType().name() : "",
                        t.getOccurredAt() != null ? t.getOccurredAt().toString() : "",
                        escapeCsv(t.getDescription()),
                        Boolean.toString(t.isDeleted())
                ))
                .collect(Collectors.joining("\n"));

        return header + "\n" + rows;
    }

    private String safeString(Object value) {
        return value != null ? value.toString() : "";
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}

