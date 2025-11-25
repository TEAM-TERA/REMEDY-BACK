package org.example.remedy.domain.dropping.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.request.VoteRequest;
import org.example.remedy.domain.dropping.application.dto.response.DroppingFindResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingSearchListResponse;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingResponse;
import org.example.remedy.domain.dropping.application.dto.response.VoteDroppingResponse;
import org.example.remedy.domain.dropping.application.service.DroppingServiceFacade;
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
    public ResponseEntity<?> getDropping(
            @PathVariable(name = "dropping-id") String id,
            @AuthenticationPrincipal AuthDetails authDetails) {
        Object response = droppingService.getDropping(id, authDetails.getUserId());
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

}
