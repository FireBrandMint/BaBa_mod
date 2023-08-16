package com.gj.baba.libraries.tinymap.util;

import com.gj.baba.libraries.tinymap.CacheAdapter;
import com.gj.baba.libraries.tinymap.ObjectCache;

public class StringCacheAdapter implements CacheAdapter<CharSequence, String> {
    @Override
    public int contentHashCode(CharSequence cs) {
        if (cs instanceof String)
            return cs.hashCode();
        int length = cs.length();
        int hash = 0;
        for (int i = 0; i < length; i++)
            hash = 31 * hash + cs.charAt(i);
        return hash;
    }

    @Override
    public String contentEquals(CharSequence cs, Object cached) {
        if (!(cached instanceof String)) return null;
        String str = (String) cached;
        return str.contentEquals(cs) ? str : null;
    }

    @Override
    public String build(CharSequence cs, ObjectCache parent) {
        return cs.toString();
    }
}
