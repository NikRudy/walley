package org.fin.walley.repository.audit;


import org.fin.walley.domain.audit.ImportExportJob;
import org.fin.walley.domain.audit.JobStatus;
import org.fin.walley.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Репозиторий для сущности ImportExportJob,
 * фиксирующей задачи импорта и экспорта данных.
 */
@Repository
public interface ImportExportJobRepository extends JpaRepository<ImportExportJob, Long> {
    /**
     * Все задачи конкретного пользователя, отсортированные
     * по времени запуска (от новых к старым).
     */
    List<ImportExportJob> findByUserOrderByStartedAtDesc(User user);


    /**
     * Задачи пользователя с указанным статусом (SUCCESS/FAILED),
     * отсортированные по времени запуска.
     */
    List<ImportExportJob> findByUserAndStatusOrderByStartedAtDesc(User user, JobStatus status);
}