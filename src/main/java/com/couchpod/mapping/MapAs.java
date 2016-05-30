package com.couchpod.mapping;

import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MapAs {
    public static <T> Type list() {
        return new TypeToken<List<T>>() {
        }.getType();
    }
}