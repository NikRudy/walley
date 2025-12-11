package org.fin.walley.dto.importexport;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fin.walley.domain.audit.JobFormat;


import java.time.LocalDate;


/**
 * DTO запроса на экспорт данных.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportRequestDto {


    @NotNull(message = "{validation.export.format.notNull}")
    private JobFormat format;


    /**
     * Период может быть обязательным для UI (например, экспорт только
     * за указанный интервал) – в таком случае обе даты помечаем
     * @NotNull и дополнительно проверяем, что dateFrom <= dateTo
     * на уровне бизнес-логики или через class-level валидатор.
     */
    @NotNull(message = "{validation.export.dateFrom.notNull}")
    private LocalDate dateFrom;


    @NotNull(message = "{validation.export.dateTo.notNull}")
    private LocalDate dateTo;


    private Long accountId;


    private boolean includeDeleted;
}