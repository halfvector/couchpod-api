package com.couchpod.mapping;

import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Mapping {
    /**
     * Allow easy containerization with ModelMapper
     * @param <T> container type (eg: List)
     * @return helper object for ModelMapper.map() function
     */
    public static <T> Type list() {
        return new TypeToken<List<T>>() {
        }.getType();
    }

    /**
     * Use Java 8 Stream API to map entities in a list
     */
    public static <T, R> List<R> map(List<T> input, Function<T, R> mapper) {
        return input.stream().map(mapper).collect(Collectors.toList());
    }
}