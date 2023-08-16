package com.gj.baba.util;

import net.minecraft.util.math.BlockPos;

public class GIntVector
{

    public int x,y;

    public GIntVector (int _x, int _y)
    {
        x = _x;
        y = _y;
    }

    @Override
    public int hashCode() {
        int tmp = ( y +  ((x+1)/2));
        return x + ( tmp * tmp );
    }

    private boolean equalsVec (GIntVector vec)
    {
        return this.x == vec.x & this.y == vec.y;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof GIntVector)
            result = this.equalsVec((GIntVector) obj);

        return result;
    }

    public String toString()
    {
        return Integer.toString(x) + '|' + Integer.toString(x);
    }
}
