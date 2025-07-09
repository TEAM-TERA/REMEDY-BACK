package org.example.remedy.domain.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.auth.dto.AuthRegisterRequest;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private boolean gender; // true : 남성, false : 여성

    public User(String username, String password, String email, LocalDate birthDate, boolean gender) {
        this.username = username;
        this.password = password;
        this.profileImage = "https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfODMg/MDAxNjA0MjI4ODc1MDgz.gQ3xcHrLXaZyxcFAoEcdB7tJWuRs7fKgOxQwPvsTsrUg.0OBtKHq2r3smX5guFQtnT7EDwjzksz5Js0wCV4zjfpcg.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%EB%B3%B4%EB%9D%BC.jpg?type=w400";
        this.email = email;
        this.role = Role.ROLE_ADMIN;
        this.provider = Provider.SELF;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public static User newInstance(AuthRegisterRequest req, String password) {
        return new User(
                req.username(),
                password,
                req.email(),
                req.birthDate(),
                req.gender()
        );
    }

    public void updateProfile(UserProfileUpdateRequest req) {
        if(req.username() != null && !this.username.equals(req.username())) this.username = req.username();
        if(req.birthDate() != null && !this.birthDate.equals(req.birthDate())) this.birthDate = req.birthDate();
        if(req.gender() != null && !this.gender == req.gender()) this.gender = req.gender();
    }

}
