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
import org.fin.walley.dto.importexport.ImportResultDto;
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
 * Базовая реализация сервиса импорта.
 * Логика разбора файлов (JSON/CSV) может быть расширена в дальнейшем;
 * сейчас реализована инфраструктура логирования ImportExportJob.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ImportServiceImpl implements ImportService {


    private final ImportExportJobRepository jobRepository;
    private final UserRepository userRepository;


    @Override
    public ImportResultDto importTransactions(Long userId, byte[] fileContent, JobFormat format) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));


        ImportExportJob job = new ImportExportJob();
        job.setUser(user);
        job.setType(JobType.IMPORT);
        job.setFormat(format);
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setStartedAt(LocalDateTime.now());


        job = jobRepository.save(job);


        try {
// Заглушка: в рамках курсовой основная цель — показать инфраструктуру.
// Здесь в дальнейшем можно реализовать парсинг JSON/CSV и создание транзакций.
            ImportResultDto result = ImportResultDto.builder()
                    .totalRecords(0)
                    .successCount(0)
                    .failedCount(0)
                    .message("Import logic not implemented yet")
                    .build();


            job.setStatus(JobStatus.SUCCESS);
            job.setFinishedAt(LocalDateTime.now());
            jobRepository.save(job);


            return result;
        } catch (RuntimeException ex) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(ex.getMessage());
            job.setFinishedAt(LocalDateTime.now());
            jobRepository.save(job);
            throw ex;
        }
    }
}