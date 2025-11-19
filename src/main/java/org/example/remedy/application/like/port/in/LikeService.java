package org.example.remedy.application.like.port.in;

import org.example.remedy.application.like.dto.response.LikeDroppingResponse;
import org.example.remedy.domain.user.User;

import java.util.List;

public interface LikeService {

    boolean toggleLike(User user, String droppingId);

    long getLikeCountByUser(User user);

    long getLikeCountByDropping(String droppingId);

    List<LikeDroppingResponse> getLikeDroppingsDetailByUser(User user);
}
