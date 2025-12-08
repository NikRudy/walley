package org.fin.walley.domain.finance;

package org.fin.walley.domain.finance;


import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.common.AbstractAuditableEntity;
import org.fin.walley.domain.user.User;


import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Финансовая операция (доход или расход), принадлежащая пользователю.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends AbstractAuditableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;


    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;


    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "PLN";


    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;


    @Column(name = "description")
    private String description;
}
