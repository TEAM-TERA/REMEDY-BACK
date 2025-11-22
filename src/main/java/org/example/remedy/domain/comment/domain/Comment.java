package org.example.remedy.domain.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.user.domain.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "dropping_id", nullable = false)
    private String droppingId;

    public Comment(String content, User user, String droppingId) {
        this.content = content;
        this.user = user;
        this.droppingId = droppingId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
