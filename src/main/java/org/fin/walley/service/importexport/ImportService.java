package org.fin.walley.service.importexport;


import org.fin.walley.dto.importexport.ImportResultDto;


import java.io.InputStream;


/**
 * Сервис импорта финансовых данных (JSON/CSV) для пользователя.
 * <p>
 * На уровне сервисного слоя мы работаем с абстракцией InputStream,
 * не завязываясь на конкретный веб-фреймворк (MultipartFile и т.п.).
 */
public interface ImportService {


    /**
     * Импорт транзакций из потока данных для указанного пользователя.
     * <p>
     * Реализация должна:
     * - определить формат (например, по расширению/параметру);
     * - распарсить файл (через Jackson/OpenCSV);
     * - применить бизнес-правила и сохранить данные;
     * - сформировать ImportResultDto с информацией об успехах/ошибках.
     *
     * @param userId идентификатор пользователя, для которого осуществляется импорт
     * @param inputStream поток данных файла
     * @param originalFileName исходное имя файла (для логирования/аудита)
     */
    ImportResultDto importTransactions(Long userId, InputStream inputStream, String originalFileName);
}