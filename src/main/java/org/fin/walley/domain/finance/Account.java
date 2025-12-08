package org.fin.walley.domain.finance;

import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.common.AbstractAuditableEntity;
import org.fin.walley.domain.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Счёт пользователя (логический или физический: карта, наличные и т.п.).
 */

@Entity
@Table(name = "accounts",
uniqueConstraints = {
        @UniqueConstraint(name = "uq_accounts_user_name", columnNames = {"user_id","name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "PLN";

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();


}
