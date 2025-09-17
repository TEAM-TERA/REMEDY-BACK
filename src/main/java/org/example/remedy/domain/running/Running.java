package org.example.remedy.domain.running;

import jakarta.persistence.*;
import lombok.*;
import org.example.remedy.domain.user.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "runnings")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Running {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double distanceKm;

    @Column(nullable = false)
    private int durationSec;

    private String songId;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;
}