package org.example.remedy.application.achievement.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class AchievementNotCompletedException extends BusinessBaseException {
    public static final AchievementNotCompletedException INSTANCE = new AchievementNotCompletedException();

    private AchievementNotCompletedException() {
        super(ErrorCode.ACHIEVEMENT_NOT_COMPLETED);
    }
}