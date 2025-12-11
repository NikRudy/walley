package org.fin.walley.dto.importexport;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;


/**
 * DTO результата операции импорта.
 * <p>
 * Позволяет фронтенду/клиенту понять, сколько записей
 * было успешно импортировано, сколько — с ошибками,
 * и какие ошибки возникли.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResultDto {


    private int totalRecords;


    private int successCount;


    private int failedCount;


    /**
     * Человеко-читаемые сообщения об ошибках (например,
     * "Строка 5: некорректный формат суммы" и т.п.).
     */
    private List<String> errorMessages;
}