package org.fin.walley.dto.importexport;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.fin.walley.domain.audit.JobFormat;


import java.time.LocalDate;


/**
 * Параметры запроса на экспорт транзакций.
 * dateFrom/dateTo и accountId могут быть null (фильтры опциональны),
 * а вот формат выгрузки обязателен.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportRequestDto {


    /**
     * Начало периода (включительно). Может быть null.
     */
    private LocalDate dateFrom;


    /**
     * Конец периода (включительно). Может быть null.
     */
    private LocalDate dateTo;


    /**
     * Ограничение по счёту. Если null – все счета пользователя.
     */
    private Long accountId;


    /**
     * Формат выгрузки (JSON или CSV).
     */
    @NotNull(message = "{export.format.not-null}")
    private JobFormat format;


    /**
     * Флаг включения логически удалённых транзакций в выгрузку.
     */
    private boolean includeDeleted;
}