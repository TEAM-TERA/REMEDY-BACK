package org.example.remedy.application.achievement.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class AchievementNotFoundException extends NotFoundException {
    public static final AchievementNotFoundException INSTANCE = new AchievementNotFoundException();

    private AchievementNotFoundException() {
        super(ErrorCode.ACHIEVEMENT_NOT_FOUND);
    }
}