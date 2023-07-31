package com.gj.baba.blocks;

import com.gj.baba.blocks.tile_entities.TileEntityGasses;
import com.gj.baba.init.BlockInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockGasses extends BlockBase
{
    static Random gasRandom = new Random(System.nanoTime());

    static int gasBus = 0;

    public BlockGasses(String name, Material material, CreativeTabs tab) {
        super(name, material, tab);
    }

    @Override
    public int tickRate(World worldIn)
    {
        return 20;
    }

    @Override
    public boolean requiresUpdates() {
        return super.requiresUpdates();
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if(worldIn.isRemote) return;
        BlockPos bPos = pos;

        BlockPos nextPos;

        if(gasRandom.nextBoolean())
            nextPos = bPos.add(gasRandom.nextInt(3) - 1, gasRandom.nextInt(3) - 1, 0);
        else
            nextPos = bPos.add(0, gasRandom.nextInt(3) - 1, gasRandom.nextInt(3) - 1);

        if(worldIn.isBlockLoaded(nextPos) && worldIn.isAirBlock(nextPos))
        {
            if(nextPos.getY() < worldIn.getPrecipitationHeight(nextPos).getY())
                worldIn.setBlockState(nextPos, BlockInit.BLOCK_GAS.getDefaultState());

            worldIn.setBlockState(bPos, Blocks.AIR.getDefaultState());
        }
        else
        {
            this.scheduleUpdate(worldIn, pos);
        }

        super.updateTick(worldIn, pos, state, random);
    }

    @Override
    public boolean getTickRandomly()
    {
        return false;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.scheduleUpdate(worldIn, pos);

        super.onBlockAdded(worldIn, pos, state);
    }

    public void OnBreak(World worldIn, BlockPos pos, IBlockState state)
    {
        /*for(EntityPlayer player : worldIn.playerEntities)
        {
            player.sendMessage(new TextComponentString("Terminated."));
        }*/
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Items.AIR, 0);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {

        if (!worldIn.isRemote && player.capabilities.isCreativeMode)
        {
            OnBreak(worldIn, pos, state);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if(!worldIn.isRemote) OnBreak(worldIn, pos, state);
        super.breakBlock(worldIn, pos, state);
    }

    private void scheduleUpdate(World worldIn, BlockPos pos)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + gasBus);

        ++gasBus;

        if(gasBus > this.tickRate(worldIn)) gasBus = 0;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }

    /*public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }*/

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return false;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Nullable
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityGasses();
    }
}
