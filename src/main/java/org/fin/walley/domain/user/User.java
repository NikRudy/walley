package org.fin.walley.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.audit.ImportExportJob;
import org.fin.walley.domain.common.AbstractAuditableEntity;
import org.fin.walley.domain.finance.Account;
import org.fin.walley.domain.finance.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Пользователь системы Walley.
 * Является владельцем счетов, транзакций и задач импорта/экспорта.
 */


@Entity
@Table(name = "users",
uniqueConstraints = {
@UniqueConstraint(name = "uq_users_username", columnNames = "username"),
@UniqueConstraint(name = "uq_users_email", columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;


    /**
     * Роли пользователя (ROLE_USER, ROLE_ADMIN и т.п.).
     */

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();



    /**
     * Счета пользователя (наличные, карты и т.п.).
     */
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();


    /**
     * Финансовые операции пользователя.
     */
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * Финансовые операции пользователя.
     */
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImportExportJob> importExportJobs = new ArrayList<>();
}
