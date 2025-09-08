package org.example.remedy.presentation.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.comment.dto.response.CommentResponse;
import org.example.remedy.application.comment.port.in.CommentService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.comment.dto.request.CommentUpdateRequest;
import org.example.remedy.presentation.comment.dto.request.CreateCommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<Void> createComment(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        commentService.createComment(request.content(), authDetails.getUser(), request.droppingId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/drop/{droppingId}")
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
}
