package org.example.remedy.domain.user.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWithdrawalScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteExpiredWithdrawalUsers(){
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        List<User> userWithdrawal = userRepository.findUsersToDeletePermanently(cutoffDate);

        if (!userWithdrawal.isEmpty()) {
            userRepository.deleteAll(userWithdrawal);
        }

    }

}
