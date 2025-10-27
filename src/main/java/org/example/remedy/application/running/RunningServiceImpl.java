package org.example.remedy.application.running;

import lombok.RequiredArgsConstructor;

import org.example.remedy.application.running.dto.response.RunningResponse;
import org.example.remedy.application.running.exception.RunningAlreadyExistsException;
import org.example.remedy.application.running.exception.RunningNotFoundException;
import org.example.remedy.application.running.port.in.RunningService;
import org.example.remedy.application.running.port.out.RunningPersistencePort;
import org.example.remedy.domain.running.Running;
import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.running.dto.request.RunningRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunningServiceImpl implements RunningService {

    private final RunningPersistencePort runningPersistencePort;

    @Override
    public void save(User user, RunningRequest request) {

        if (runningPersistencePort.existsByUserAndSongId(user, request.songId())) {
            throw RunningAlreadyExistsException.EXCEPTION;
        }


        Running running = Running.builder()
                .user(user)
                .distanceKm(request.distanceKm())
                .durationSec(request.durationSec())
                .songId(request.songId())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .build();

        runningPersistencePort.save(running);
    }

    @Override
    public List<RunningResponse> getRunningRecords(User user) {
        List<Running> runs = runningPersistencePort.findByUser(user);

        if (runs.isEmpty()) {
            throw RunningNotFoundException.EXCEPTION;
        }

        return runs.stream()
                .map(RunningResponse::from)
                .toList();
    }
}