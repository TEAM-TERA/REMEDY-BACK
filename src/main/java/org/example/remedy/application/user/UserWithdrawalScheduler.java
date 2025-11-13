package org.example.remedy.application.user;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.user.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWithdrawalScheduler {
    private final UserPersistencePort userPersistencePort;

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteExpiredWithdrawalUsers(){
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        List<User> userWithdrawal = userPersistencePort.findUsersToDeletePermanently(cutoffDate);

        if (!userWithdrawal.isEmpty()) {
            userPersistencePort.deleteAll(userWithdrawal);
        }

    }

}
