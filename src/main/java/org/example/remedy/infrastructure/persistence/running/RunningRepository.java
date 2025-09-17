package org.example.remedy.infrastructure.persistence.running;

import org.example.remedy.domain.running.Running;
import org.example.remedy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RunningRepository extends JpaRepository<Running, Long> {
    List<Running> findByUser(User user);
}
