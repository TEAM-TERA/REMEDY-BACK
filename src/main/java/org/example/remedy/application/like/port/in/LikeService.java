package org.example.remedy.application.like.port.in;

public interface LikeService {

    boolean toggleLike(Long userId, String droppingId);
    long getLikeCountByUser(Long userId);
    long getLikeCountByDropping(String droppingId);
}
