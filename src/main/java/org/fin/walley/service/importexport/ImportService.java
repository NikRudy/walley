package org.fin.walley.service.importexport;

import org.fin.walley.domain.audit.JobFormat;
import org.fin.walley.dto.importexport.ExportRequestDto;
import org.fin.walley.dto.importexport.ImportResultDto;

/**
 * Сервис для операций импорта данных (например, транзакций)
 * из JSON/CSV-файлов.
 */
public interface ImportService {

    /**
     * Импорт данных для указанного пользователя из файла заданного формата.
     * Реализация должна создавать запись ImportExportJob и возвращать
     * агрегированный результат импорта.
     */
    ImportResultDto importTransactions(Long userId, byte[] fileContent, JobFormat format);
}
