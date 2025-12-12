package org.fin.walley.domain.finance;

import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.common.AbstractAuditableEntity;
import org.fin.walley.domain.user.User;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Владелец счёта.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Название счёта (например, «Наличные», «Карта VISA»).
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Опциональное описание счёта.
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Текущий баланс счёта.
     */
    @Column(name = "current_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    /**
     * Флаг активности (архив/активен).
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Транзакции, привязанные к этому счёту.
     * Обратная сторона связи Transaction.account.
     */
    @OneToMany(mappedBy = "account")
    @Builder.Default
    private Set<Transaction> transactions = new HashSet<>();
}
