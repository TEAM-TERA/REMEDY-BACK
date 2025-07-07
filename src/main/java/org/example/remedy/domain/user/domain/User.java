package org.example.remedy.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;

import java.time.LocalDate;
import java.util.Date;

@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImage;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider = Provider.SELF;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private boolean gender; // true : 남성, false : 여성

    public void updateProfile(String username, Boolean gender) {
        this.username = username;
        this.gender = gender;
    }
}
