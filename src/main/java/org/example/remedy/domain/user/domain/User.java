package org.example.remedy.domain.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
@DynamicInsert
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 15)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, columnDefinition = "TEXT")
	@ColumnDefault("'https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfODMg/MDAxNjA0MjI4ODc1MDgz.gQ3xcHrLXaZyxcFAoEcdB7tJWuRs7fKgOxQwPvsTsrUg.0OBtKHq2r3smX5guFQtnT7EDwjzksz5Js0wCV4zjfpcg.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%EB%B3%B4%EB%9D%BC.jpg?type=w400'")
    private String profileImage;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	@ColumnDefault("'ROLE_USER'")
    private Role role;

    @Column
    private LocalDate birthDate;

    @Column
    private Boolean gender; // true : 남성, false : 여성

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	@ColumnDefault("'JOIN'")
    private Status status;

    @Column
    private LocalDateTime withdrawalDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	@ColumnDefault("'LOCAL'")
    private OAuth2Provider provider;

    @Column
    private String providerId;

    public User(String username, String password, String email, LocalDate birthDate, boolean gender) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public User(String username, String email, String profileImage, LocalDate birthDate, Boolean gender, OAuth2Provider provider, String providerId) {
        this.username = username;
		if(!profileImage.isBlank()) {
			this.profileImage = profileImage;
		}
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void updateProfile(String username, LocalDate birthDate, Boolean gender) {
        if(username != null && !this.username.equals(username)) this.username = username;
        if(birthDate != null && !this.birthDate.equals(birthDate)) this.birthDate = birthDate;
        if(gender != null && !gender.equals(this.gender)) this.gender = gender;
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImage = imageUrl;
    }

    public void withdrawal(){
        this.status = Status.WITHDRAWAL;
        this.withdrawalDate = LocalDateTime.now();
    }

    public void reactivate(){
        this.status = Status.JOIN;
        this.withdrawalDate = null;
    }

}
