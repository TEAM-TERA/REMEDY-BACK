package org.example.remedy.domain.comment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.user.User;

@Entity
@NoArgsConstructor
@Table(name = "comments")
@AllArgsConstructor
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
}
