package org.example.remedy.presentation.dropping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.port.in.DroppingServiceFacade;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.presentation.dropping.dto.request.VoteDroppingCreateRequest;
import org.example.remedy.presentation.dropping.dto.request.VoteRequest;
import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.VoteDroppingResponse;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/droppings")
@RequiredArgsConstructor
public class DroppingController {
    private final DroppingServiceFacade droppingService;

    @PostMapping
    public ResponseEntity<Void> createDropping(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody DroppingCreateRequest request) {
        droppingService.createDropping(authDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{dropping-id}")
    public ResponseEntity<DroppingFindResponse> getDropping(@PathVariable(name = "dropping-id") String id) {
        DroppingFindResponse response = droppingService.getDropping(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<DroppingSearchListResponse> searchDroppings(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        DroppingSearchListResponse response = droppingService.searchDroppings(longitude, latitude);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{dropping-id}")
    public ResponseEntity<Void> deleteDropping(
            @PathVariable(name = "dropping-id") String id,
            @AuthenticationPrincipal AuthDetails authDetails) {
        droppingService.deleteDropping(id, authDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vote")
    public ResponseEntity<Void> createVoteDropping(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody VoteDroppingCreateRequest request) {
        droppingService.createVoteDropping(authDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{dropping-id}/vote")
    public ResponseEntity<Void> vote(
            @PathVariable(name = "dropping-id") String id,
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody VoteRequest request) {
        droppingService.vote(id, authDetails.getUserId(), request.songId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{dropping-id}/vote")
    public ResponseEntity<Void> cancelVote(
            @PathVariable(name = "dropping-id") String id,
            @AuthenticationPrincipal AuthDetails authDetails) {
        droppingService.cancelVote(id, authDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{dropping-id}/vote")
    public ResponseEntity<VoteDroppingResponse> getVoteDropping(
            @PathVariable(name = "dropping-id") String id,
            @AuthenticationPrincipal AuthDetails authDetails) {
        VoteDroppingResponse response = droppingService.getVoteDropping(id, authDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
