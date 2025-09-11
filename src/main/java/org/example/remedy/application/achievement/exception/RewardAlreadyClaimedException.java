package org.example.remedy.application.achievement.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class RewardAlreadyClaimedException extends BusinessBaseException {
    public static final RewardAlreadyClaimedException INSTANCE = new RewardAlreadyClaimedException();

    private RewardAlreadyClaimedException() {
        super(ErrorCode.REWARD_ALREADY_CLAIMED);
    }
}