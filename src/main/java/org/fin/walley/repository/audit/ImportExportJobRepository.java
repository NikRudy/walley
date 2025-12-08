package org.fin.walley.repository.audit;


import org.fin.walley.domain.audit.ImportExportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий для сущности ImportExportJob,
 * фиксирующей задачи импорта и экспорта данных.
 */
@Repository
public interface ImportExportJobRepository extends JpaRepository<ImportExportJob, Long> {
// Методы выборки задач по пользователю и статусу
// будут добавлены на последующем подэтапе.
}