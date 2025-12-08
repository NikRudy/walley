package org.fin.walley.domain.audit;

import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.common.AbstractAuditableEntity;
import org.fin.walley.domain.user.User;


import java.time.LocalDateTime;


/**
 * Задача импорта или экспорта данных (JSON/CSV) с результатами и статистикой.
 */
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


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 20)
    private JobType jobType; // IMPORT / EXPORT


    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 20)
    private JobFormat format; // JSON / CSV


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status; // SUCCESS / FAILED


    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;


    @Column(name = "finished_at")
    private LocalDateTime finishedAt;


    @Column(name = "total_records")
    private Integer totalRecords;


    @Column(name = "success_count")
    private Integer successCount;


    @Column(name = "error_count")
    private Integer errorCount;


    @Column(name = "details")
    private String details;
}
