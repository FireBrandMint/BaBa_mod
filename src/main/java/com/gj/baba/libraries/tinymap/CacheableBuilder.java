package com.gj.baba.libraries.tinymap;

public interface CacheableBuilder<B, T> {
    T build();

    CacheAdapter<B, T> adapter();
}
