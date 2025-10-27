package org.example.remedy.application.running.port.in;

import org.example.remedy.application.running.dto.response.RunningResponse;
import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.running.dto.request.RunningRequest;

import java.util.List;

public interface RunningService {

    void save(User user, RunningRequest request);

    List<RunningResponse> getRunningRecords(User user);
}
