package org.example.remedy.application.achievement.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class UserAchievementNotFoundException extends NotFoundException {
    public static final UserAchievementNotFoundException INSTANCE = new UserAchievementNotFoundException();

    private UserAchievementNotFoundException() {
        super(ErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
    }
}