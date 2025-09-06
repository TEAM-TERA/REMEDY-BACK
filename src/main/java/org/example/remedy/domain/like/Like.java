package org.example.remedy.domain.like;

import jakarta.persistence.*;
import org.example.remedy.domain.user.User;


@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "dropping_id", nullable = false)
    private String droppingId;

    protected Like() {}

    public Like(User user, String droppingId) {
        this.user = user;
        this.droppingId = droppingId;
    }
}

