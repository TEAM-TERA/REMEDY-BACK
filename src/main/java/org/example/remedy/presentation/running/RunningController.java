package org.example.remedy.presentation.running;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.running.dto.response.RunningResponse;
import org.example.remedy.application.running.port.in.RunningService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.running.dto.request.RunningRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/running")
@RequiredArgsConstructor
public class RunningController {

    private final RunningService runningService;


    @PostMapping("/record")
    public ResponseEntity<Void> saveRecord(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody RunningRequest request) {

        runningService.save(authDetails.getUser(), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/records")
    public List<RunningResponse> getRecord(@AuthenticationPrincipal AuthDetails authDetails) {

        return runningService.getRunningRecords(authDetails.getUser());
    }
}