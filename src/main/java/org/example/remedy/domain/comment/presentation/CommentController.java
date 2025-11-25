package org.example.remedy.domain.comment.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.comment.application.dto.response.CommentResponse;
import org.example.remedy.domain.comment.application.service.CommentService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.domain.comment.application.dto.request.CommentUpdateRequest;
import org.example.remedy.domain.comment.application.dto.request.CreateCommentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        commentService.createComment(request.content(), authDetails.getUser(), request.droppingId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/droppings/{droppingId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByDropping(
            @PathVariable String droppingId
    ) {
        return ResponseEntity.ok(commentService.getCommentsByDropping(droppingId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateComment(authDetails.getUserId(), commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthDetails authDetails
    ){
        commentService.deleteComment(authDetails.getUserId(), commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/{droppingId}")
    public ResponseEntity<Long> getCommentCount(@PathVariable String droppingId) {
        long count = commentService.countByDroppingId(droppingId);

        return ResponseEntity.ok(count);
    }
}
