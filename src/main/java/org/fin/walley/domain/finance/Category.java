package org.fin.walley.domain.finance;

import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.common.AbstractAuditableEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * Верхнеуровневая категория финансовой операции (Еда, Транспорт и т.д.).
 */
@Entity
@Table(name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_categories_name_type", columnNames = {"name", "type"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AbstractAuditableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type; // INCOME / EXPENSE


    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Subcategory> subcategories = new ArrayList<>();


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
