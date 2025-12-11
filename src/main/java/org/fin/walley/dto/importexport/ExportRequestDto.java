package org.fin.walley.dto.importexport;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fin.walley.domain.audit.JobFormat;


import java.time.LocalDate;


/**
 * DTO запроса на экспорт данных.
 * <p>
 * Позволяет задать формат (JSON/CSV) и базовые фильтры
 * по периоду и счёту/типу для формирования выгрузки.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportRequestDto {


    /**
     * Формат выгрузки (JSON или CSV).
     */
    private JobFormat format;


    /**
     * Начало периода (включительно).
     */
    private LocalDate dateFrom;


    /**
     * Конец периода (включительно).
     */
    private LocalDate dateTo;


    /**
     * Опциональный фильтр по счёту.
     */
    private Long accountId;


    /**
     * Признак включения логически удалённых транзакций в выгрузку.
     */
    private boolean includeDeleted;
}