package org.example.remedy.domain.dropping.repository;

import org.example.remedy.domain.dropping.domain.Dropping;

import java.util.List;

@FunctionalInterface
public interface FindActiveDroppings {
    List<Dropping> findActiveDroppingsWithinRadius(double latitude, double longitude);
}
