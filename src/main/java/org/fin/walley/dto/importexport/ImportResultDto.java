package org.fin.walley.dto.importexport;


import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;


/**
 * DTO результата операции импорта.
 * <p>
 * Обычно используется только как ответ (outbound), но
 * аннотации @PositiveOrZero здесь задают базовые инварианты,
 * которые могут проверяться в тестах или при внутренней
 * валидации.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResultDto {


    @PositiveOrZero(message = "{validation.import.totalRecords.positiveOrZero}")
    private int totalRecords;


    @PositiveOrZero(message = "{validation.import.successCount.positiveOrZero}")
    private int successCount;


    @PositiveOrZero(message = "{validation.import.failedCount.positiveOrZero}")
    private int failedCount;


    private List<String> errorMessages;
}