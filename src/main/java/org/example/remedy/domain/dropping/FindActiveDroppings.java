package org.example.remedy.domain.dropping;

import java.util.List;

@FunctionalInterface
public interface FindActiveDroppings {
    List<Dropping> findActiveDroppingsWithinRadius(double latitude, double longitude);
}
