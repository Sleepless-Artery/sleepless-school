package org.sleepless_artery.auth_service.role.model;

import jakarta.persistence.*;
import lombok.*;


/**
 * Entity representing a user role.
 */
@Entity
@Table(name = "role")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
}