package com.gj.baba.Items;

import com.gj.baba.capabilities.StatsSystem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RequiemBovid extends BaseItem
{
    int cooldown = 250;
    public RequiemBovid(String name, CreativeTabs creativeTab) {
        super(name, creativeTab);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Hunger comes a fifth slower.");
        tooltip.add("");
        tooltip.add("\"For my kin and alike, remain strong.\"");
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if(worldIn.isRemote) return;
        
        if(entityIn instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityIn;
            StatsSystem.IBabaStats stats = player.getCapability(StatsSystem.CAPABILITY, StatsSystem.sideFinal);
            if(!stats.hasTempFlag(-325))
            {
                stats.addStatModifier(StatsSystem.EnumStats.SATURATION_DEPLETION, 0.8f, StatsSystem.OperationType.Mul);
                stats.setTempFlag(-325);
            }
            /*
            CooldownTracker ct = player.getCooldownTracker();
            if(player.isSprinting() && !player.getCooldownTracker().hasCooldown(this))
            {
                FoodStats fs = player.getFoodStats();
                //fucking hack because mojang is ass
                //why the fuck is FoodStats.setFoodSaturationLevel
                //client side? what retarded ignorant moron is
                //responsible for this?
                fs.addStats(1, 1f);
                fs.addStats(-1, 0f);
                ct.setCooldown(this, cooldown);
            }

             */
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }
}
