package org.fin.walley.domain.finance;

import jakarta.persistence.*;
import lombok.*;
import org.fin.walley.domain.common.AbstractAuditableEntity;


import java.util.ArrayList;
import java.util.List;


/**
 * Подкатегория внутри категории финансовых операций.
 */
@Entity
@Table(name = "subcategories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_subcategories_cat_name", columnNames = {"category_id", "name"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subcategory extends AbstractAuditableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;


    @OneToMany(mappedBy = "subcategory", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
