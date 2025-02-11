package org.romanzhula.user_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "nickname", unique = true, nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

}
