package org.example.remedy.application.like.port.in;

import org.example.remedy.domain.user.User;

public interface LikeService {

    boolean toggleLike(User user, String droppingId);

    long getLikeCountByUser(User user);

    long getLikeCountByDropping(String droppingId);
}
