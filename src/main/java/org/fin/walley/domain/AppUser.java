package org.fin.walley.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(name = "uk_user_username", columnNames = "username"))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Column(nullable = false)
    private String username;


    @Column(nullable = false)
    private String passwordHash;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    @Column(nullable = false)
    private boolean enabled;


}