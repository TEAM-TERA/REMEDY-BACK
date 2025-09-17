package org.example.remedy.application.running.port.out;

import org.example.remedy.domain.running.Running;
import org.example.remedy.domain.user.User;

import java.util.List;

public interface RunningPersistencePort {

    void save(Running running);

    List<Running> findByUser(User user);

    boolean existsByUserAndSongId(User user, String songId);

}
