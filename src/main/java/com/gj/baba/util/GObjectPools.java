package com.gj.baba.util;

import com.gj.baba.capabilities.GasSystem;
import com.gj.baba.libraries.tinymap.TinyMapBuilder;

import java.util.Stack;

public class GObjectPools
{
    private static Stack<TinyMapBuilder<GChunkBlockCoords, GasSystem.Gas>> gasMatrixMaps = new Stack<TinyMapBuilder<GChunkBlockCoords, GasSystem.Gas>>();
    public static void storeGasMatrixMap(TinyMapBuilder<GChunkBlockCoords, GasSystem.Gas> map)
    {
        gasMatrixMaps.push(map);
    }

    public static TinyMapBuilder<GChunkBlockCoords, GasSystem.Gas> getGasMatrixMap(int sizeIfNotFound)
    {
        if(gasMatrixMaps.size() > 0) return gasMatrixMaps.pop();

        return new TinyMapBuilder<GChunkBlockCoords, GasSystem.Gas>(sizeIfNotFound);
    }
}
