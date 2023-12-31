package com.gj.baba.libraries.tinymap;

public interface CacheAdapter<B, T> {
    int contentHashCode(B builder);

    T contentEquals(B builder, Object cached);

    T build(B builder, ObjectCache cache);
}
