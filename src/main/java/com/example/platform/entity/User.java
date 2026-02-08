//Entity JPA që mapohet te tabela users.
//
//Ka fushat: emri, atesia, mbiemri, nrTel, datelindja, email, password, role, photoUrl, etj.
package com.example.platform.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emri;
    private String atesia;
    private String mbiemri;

    @Column(unique = true)
    private String nrTel;

    private LocalDate datelindja;

    @Column(unique = true)
    private String email;

    private String password;
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    private Role role;
}


