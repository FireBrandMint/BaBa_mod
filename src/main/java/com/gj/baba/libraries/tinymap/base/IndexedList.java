package com.gj.baba.libraries.tinymap.base;

import java.util.List;

public interface IndexedList<T> extends List<T>, IndexedCollection<T> {
    T removeLast();

    @Override
    IndexedList<T> subList(int fromIndex, int toIndex);
}
