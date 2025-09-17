package org.example.remedy.infrastructure.persistence.running;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.running.port.out.RunningPersistencePort;
import org.example.remedy.domain.running.Running;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaRunningAdapter implements RunningPersistencePort {

    private final RunningRepository runningRepository;

    @Override
    public void save(Running running) {
        runningRepository.save(running);
    }

    @Override
    public List<Running> findByUser(User user) {
        return runningRepository.findByUser(user);
    }

    @Override
    public boolean existsByUserAndSongId(User user, String songId) {
        return runningRepository.existsByUserAndSongId(user, songId);
    }

}
