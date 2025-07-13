package org.example.remedy.domain.user;

import org.example.remedy.domain.user.domain.User;

import java.time.LocalDate;

public class UserTestFactory {

    public static User create(String username, String email) {
        return new User(
                username,
                "password7777",
                email,
                LocalDate.of(2008, 7, 31),
                true
        );
    }
}