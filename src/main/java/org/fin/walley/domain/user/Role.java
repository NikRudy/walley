package org.fin.walley.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Роль безопасности (например, ROLE_USER, ROLE_ADMIN).
 */

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Обратная сторона связи многие-ко-многим с User.
     * Маппинг управляется со стороны User (поле roles).
     */

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private List<User> users = new ArrayList<>();
}
