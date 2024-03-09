package com.gj.baba.Items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RequiemLife extends BaseItem
{

    public RequiemLife(String name, CreativeTabs creativeTab) {
        super(name, creativeTab);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Right click to trade satiety for health.");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack onHand = playerIn.getHeldItem(handIn);
        playerIn.getCooldownTracker().setCooldown(onHand.getItem(), 600);
        FoodStats fs = playerIn.getFoodStats();
        int foodLevel = fs.getFoodLevel();
        if(!worldIn.isRemote && foodLevel > 4 && playerIn.shouldHeal())
        {
            //consume until only 2 meatsticks remain
            fs.addStats(-(foodLevel - 4), 0f);
            //heals by consumed meatsticks divided by 4
            playerIn.heal((foodLevel - 4) >> 1);
        }

        playerIn.playSound(SoundEvents.ENTITY_PLAYER_HURT, 0.5f, 1.3f);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, onHand);
    }

    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }
}
