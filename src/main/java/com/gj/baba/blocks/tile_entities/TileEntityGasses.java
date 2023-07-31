package com.gj.baba.blocks.tile_entities;

import com.gj.baba.init.BlockInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Random;

public class TileEntityGasses extends TileEntity implements ITickable
{
    static Random gasRandom = new Random(System.nanoTime());

    public TileEntityGasses()
    {
    }

    @Override
    public void update()
    {
        /*
        if(world.isRemote) return;
        BlockPos bPos = this.pos;

        BlockPos nextPos;

        if(gasRandom.nextBoolean())
            nextPos = bPos.add(gasRandom.nextInt(3) - 1, gasRandom.nextInt(3) - 1, 0);
        else
            nextPos = bPos.add(0, gasRandom.nextInt(3) - 1, gasRandom.nextInt(3) - 1);

        if(world.isBlockLoaded(nextPos) && world.isAirBlock(nextPos))
        {
            if(!world.canSeeSky(nextPos)) world.setBlockState(nextPos, BlockInit.BLOCK_GAS.getDefaultState());
            world.setBlockState(bPos, Blocks.AIR.getDefaultState());
        }*/
    }

    @Override
    public void onLoad()
    {
        if(!this.world.isRemote)
        {
            for(EntityPlayer player : this.world.playerEntities)
            {
                player.sendMessage(new TextComponentString("LOADED"));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if(compound == null)
            compound = new NBTTagCompound();
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
}
