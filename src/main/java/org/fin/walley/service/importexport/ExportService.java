package org.fin.walley.service.importexport;


import org.fin.walley.dto.importexport.ExportRequestDto;


/**
 * Сервис экспорта финансовых данных пользователя.
 */

interface ExportService {

    /**
     * Экспорт транзакций пользователя в соответствии с параметрами запроса.
     * Возвращает байтовый массив с содержимым файла.
     */
    byte[] exportTransactions(Long userId, ExportRequestDto requestDto);

    /**
     * Построение имени файла для экспорта (для заголовков Content-Disposition
     * или сохранения на диск).
     */
    String buildExportFileName(Long userId, ExportRequestDto requestDto);
}
