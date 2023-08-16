package com.gj.baba.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class GChunkBlockCoords
{
    public byte x;
    public short y;
    public byte z;

    public static GChunkBlockCoords fromNormalCoords (BlockPos pos, int chunkX, int chunkZ)
    {
        byte x = (byte) (pos.getX() - (chunkX << 4));
        short y = (short)pos.getY();
        byte z = (byte) (pos.getZ() - (chunkZ << 4));

        return new GChunkBlockCoords(x,y,z);
    }

    public static BlockPos toNormalCoords (GChunkBlockCoords pos, int chunkX, int chunkZ)
    {
        int x = (pos.x & 0xFF) + (chunkX << 4);
        int y = (int)pos.y;
        int z = (pos.z & 0xFF) + (chunkZ << 4);

        return new BlockPos(x,y,z);
    }

    public GChunkBlockCoords(byte _x, short _y, byte _z)
    {
        x = _x;
        if(_y > 2000) throw new IllegalArgumentException();
        y = _y;
        z = _z;
    }

    @Override
    public int hashCode() {
        return (int) x + ((int) y * 1000000) + ((int) z * 1000);
    }

    private boolean equalsVec (GChunkBlockCoords vec)
    {
        return this.x == vec.x & this.y == vec.y & this.z == vec.z;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof GChunkBlockCoords)
            result = this.equalsVec((GChunkBlockCoords) obj);

        return result;
    }

    public String toString()
    {
        return Integer.toString(x) + '|' + Integer.toString(y) + '|' + Integer.toString(z);
    }
}
