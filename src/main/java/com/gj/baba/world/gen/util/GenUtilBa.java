package com.gj.baba.world.gen.util;

import com.gj.baba.world.gen.WorldGenTreeBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class GenUtilBa
{

    public void genTreeLeavesLayer(int length, Block log, IBlockState leaves, World world, BlockPos middle, WorldGenTreeBase subject)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.setPos(middle);

        int originX = middle.getX();
        int originY = middle.getY();
        int originZ = middle.getZ();
        if(length > 1)
        {
            if(world.getBlockState(pos).getBlock() != log) setBlockAndNotifyAdequately(world, pos, leaves, subject);

            for(int x = -length; x < length + 1; ++x)
            {
                for(int z = -length; z < length + 1; ++z)
                {
                    int stretch = Math.abs(x) + Math.abs(z);
                    if((x == 0 & z == 0) || stretch > length + 1) continue;
                    setBlockAndNotifyAdequately(world, pos.setPos(originX + x, originY, originZ + z), leaves, subject);
                }
            }

            return;
        }

        if(length == 0)
        {
            if(world.getBlockState(pos).getBlock() != log) setBlockAndNotifyAdequately(world, pos, leaves, subject);
        } else if (length == 1)
        {
            if(world.getBlockState(pos).getBlock() != log) setBlockAndNotifyAdequately(world, pos, leaves, subject);
            for(int x = -1; x < 2; ++x)
            {
                for(int z = -1; z < 2; ++z)
                {
                    setBlockAndNotifyAdequately(world, pos.setPos(originX + x, originY, originZ + z), leaves, subject);
                }
            }
        }
    }

    public static void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state, WorldGenTreeBase subject)
    {
        if (subject.doNotify)
        {
            worldIn.setBlockState(pos, state, 3);
        }
        else
        {
            int flag = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 2| 16 : 2; //Forge: With bit 5 unset, it will notify neighbors and load adjacent chunks.
            worldIn.setBlockState(pos, state, flag);
        }
    }
}
