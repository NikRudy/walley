package org.fin.walley.domain.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fin.walley.domain.common.AbstractAuditableEntity;
import org.fin.walley.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_export_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportExportJob extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, от имени которого выполняется импорт/экспорт.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Тип задачи: IMPORT или EXPORT.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 20)
    private JobType type;

    /**
     * Формат файла: JSON или CSV.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_format", nullable = false, length = 20)
    private JobFormat format;

    /**
     * Статус выполнения задачи.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false, length = 20)
    private JobStatus status;

    /**
     * Время старта задачи.
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * Время завершения задачи.
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * Сообщение об ошибке (при неуспешном выполнении).
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
}
