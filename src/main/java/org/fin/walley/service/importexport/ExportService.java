package org.fin.walley.service.importexport;


import org.fin.walley.dto.importexport.ExportRequestDto;


/**
 * Сервис экспорта финансовых данных пользователя.
 */
public interface ExportService {


    /**
     * Формирование выгрузки транзакций пользователя в виде массива байт,
     * который затем может быть отдан как файл (JSON/CSV) через контроллер.
     */
    byte[] exportTransactions(Long userId, ExportRequestDto requestDto);


    /**
     * Формирование рекомендованного имени файла для выгрузки (например,
     * walley-transactions-2025-01-01_2025-01-31.csv).
     */
    String buildExportFileName(Long userId, ExportRequestDto requestDto);
}