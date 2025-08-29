package org.example.remedy.global.util.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Mapper {
    public static <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

}
