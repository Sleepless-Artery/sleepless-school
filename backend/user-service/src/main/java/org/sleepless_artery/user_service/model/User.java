package org.sleepless_artery.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email_address", unique = true, nullable = false)
    private String emailAddress;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "information")
    private String information;
}
