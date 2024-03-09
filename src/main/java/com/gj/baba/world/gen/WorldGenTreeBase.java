package com.gj.baba.world.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class WorldGenTreeBase extends WorldGenAbstractTree
{
    public final boolean doNotify;

    public WorldGenTreeBase(boolean notify) {
        super(notify);
        this.doNotify = notify;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        return false;
    }
}
