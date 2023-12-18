package com.gj.baba.blocks;

import com.gj.baba.blocks.tile_entities.TileEntityGasses;
import com.gj.baba.capabilities.GasSystem;
import com.gj.baba.components.substances.Substance;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockInjectGas extends BlockBase
{
    public static Random gasRandom = new Random(System.nanoTime());

    private final BlockPos[] possiblePos = new BlockPos[]
    {
            new BlockPos(0, 1, 0),
            new BlockPos(0, - 1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0 , -1)
    };

    public BlockInjectGas(String name, Material material, CreativeTabs tab) {

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
        super.updateTick(worldIn, pos, state, random);
    }

    @Override
    public boolean getTickRandomly()
    {
        return false;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        GasSystem.Gas gas = new GasSystem.Gas();

        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());

        Substance sub = Substance.getSubstance(Substance.OXYGEN_ID);
        sub.setMoles(20f);
        sub.setTemperatureK(273.15f + 20.15f);

        Substance sub2 = Substance.getSubstance(Substance.PLASMA_ID);
        sub2.setMoles(20f);
        sub2.setTemperatureK(273.15f + 20.15f);

        gas.mixWithSelf(sub);

        gas.mixWithSelf(sub2);

        GasSystem.tryInjectGas(worldIn, pos, gas);

        super.onBlockAdded(worldIn, pos, state);
    }

    public void OnBreak(World worldIn, BlockPos pos, IBlockState state)
    {
        //GasProcess.removeGas(worldIn, pos);

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