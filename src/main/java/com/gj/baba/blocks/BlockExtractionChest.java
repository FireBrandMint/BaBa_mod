package com.gj.baba.blocks;

import com.gj.baba.blocks.util.ExtractionChestInventory;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockExtractionChest extends BlockBase
{

    public BlockExtractionChest(String name, Material material, CreativeTabs tab)
    {
        super(name, material, tab);
    }
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(worldIn.isRemote) return true;
        NBTTagCompound playerData = playerIn.getEntityData();
        if(!playerData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        NBTTagCompound persistent = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if(persistent.hasKey(ExtractionChestInventory.saveTag))
        {
            ExtractionChestInventory inv = new ExtractionChestInventory(persistent);
            ItemStack mainItem = playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            if(mainItem.isEmpty())
            {
                ItemStack withdraw = inv.popItem();
                if(withdraw == null) return true;
                worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY + playerIn.eyeHeight, playerIn.posZ, withdraw));
                inv.writeNBT(persistent);
            }
            else
            {
                ItemStack tried = inv.addItem(mainItem, playerIn);
                if(mainItem.isItemEqual(tried) && mainItem.getCount() == tried.getCount()) return true;

                playerIn.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, tried);
                inv.writeNBT(persistent);
            }

            return true;
        }
        else
        {
            ExtractionChestInventory fodder = new ExtractionChestInventory(27);
            fodder.writeNBT(persistent);
            return false;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add("A dimensional bank storage device for mining.");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GREEN + "Right click with a item to store it.");
        tooltip.add(TextFormatting.DARK_GREEN + "Right click with an empty hand to withdraw items.");
    }
}
