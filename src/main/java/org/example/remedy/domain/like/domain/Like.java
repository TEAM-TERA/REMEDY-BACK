package org.example.remedy.domain.like.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.remedy.domain.user.domain.User;


@Entity
@Table(name = "likes")
@Getter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "dropping_id", nullable = false)
    private Long droppingId;

    protected Like() {}

    public Like(User user, Long droppingId) {
        this.user = user;
        this.droppingId = droppingId;
    }
}

