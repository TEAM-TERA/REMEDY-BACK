package org.example.remedy.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
}