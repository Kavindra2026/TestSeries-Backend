package com.textseries.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;

    @Column(unique = true)
    private String email;

    private String phone;
    private String password;
    private String state;

    private String role; // ADMIN / USER

    private boolean verified; // 🔥 OTP verified
}