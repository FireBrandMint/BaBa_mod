package com.gj.baba.blocks;

import com.gj.baba.BaBa;
import com.gj.baba.Items.IHasModel;
import com.gj.baba.init.BlockInit;
import com.gj.baba.init.ItemInit;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.List;

public class BaseLog extends BlockRotatedPillar implements IHasModel
{
    BaseLeaves leaves;
    public BaseLog(String name, Material material, CreativeTabs tab, BaseLeaves leaves)
    {
        super(Material.WOOD);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);

        this.leaves = leaves;

        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(tab);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add("Basically quantum logs.");
        tooltip.add("...");
        tooltip.add("Is it just me or is it converging?");
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = 4;
        int j = 5;

        if (worldIn.isAreaLoaded(pos.add(-1, -1, -1), pos.add(1, 1, 1)))
        {
            for (BlockPos blockpos : BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1)))
            {
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (iblockstate.getBlock() == leaves)
                {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
                }
            }
        }
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getStateFromMeta(meta).withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch ((BlockLog.EnumAxis)state.getValue(BlockLog.LOG_AXIS))
                {
                    case X:
                        return state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
                    case Z:
                        return state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }

    @Override public boolean canSustainLeaves(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos){ return true; }
    @Override public boolean isWood(net.minecraft.world.IBlockAccess world, BlockPos pos){ return true; }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BlockLog.LOG_AXIS});
    }

    public int getMetaFromState(IBlockState state)
    {
        BlockLog.EnumAxis axis = state.getValue(BlockLog.LOG_AXIS);

        int result = 0;

        switch (axis)
        {
            case X:
                result = 0;
                break;
            case Y:
                result = 1;
                break;
            case Z:
                result = 2;
                break;
            case NONE:
                result = 3;
                break;
        }

        return result;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        BlockLog.EnumAxis axis = BlockLog.EnumAxis.NONE;

        switch (meta)
        {
            case 0:
                axis = BlockLog.EnumAxis.X;
                break;
            case 1:
                axis = BlockLog.EnumAxis.Y;
                break;
            case 2:
                axis = BlockLog.EnumAxis.Z;
                break;
            case 3:
                axis = BlockLog.EnumAxis.NONE;
                break;
        }

        return this.getDefaultState().withProperty(BlockLog.LOG_AXIS, axis);
    }

    @Override
    public void RegisterModels()
    {
        BaBa.Proxy.RegisterItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
