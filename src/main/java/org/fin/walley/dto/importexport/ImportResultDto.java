package org.fin.walley.dto.importexport;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fin.walley.domain.audit.JobFormat;


import java.time.LocalDate;


/**
 * Результат импорта (агрегированные показатели).
 * Это выходной DTO, поэтому аннотации валидации здесь не обязательны.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResultDto {


    private int totalRecords;


    private int successCount;


    private int failedCount;


    /**
     * Опциональное текстовое описание ошибки/предупреждения.
     */
    private String message;
}