package com.gj.baba.blocks;

import com.gj.baba.init.BlockInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlueTreeLeaves extends BaseLeaves
{
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add("Basically quantum leaves.");
        tooltip.add("...");
        tooltip.add("Is it just me or are some leaves missing?");
    }

    public BlueTreeLeaves(String name, Material material, CreativeTabs tab) {
        super(name, material, tab);
    }

    @Override
    public Block getLog() {
        return BlockInit.LOG_BLUEWORLD;
    }

    @Override
    public Item getSapling() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if(isLeavesFancy())
        {
            if(((pos.getX() + pos.getY() + pos.getZ()) & 1) == 0) return true;

            Vec3i directionVec = side.getDirectionVec();
            return blockAccess.getBlockState(pos.add(directionVec)).getBlock() != this;
        }

        return blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}
