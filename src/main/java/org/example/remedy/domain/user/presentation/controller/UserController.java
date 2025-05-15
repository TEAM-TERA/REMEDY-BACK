package org.example.prote.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.prote.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
}