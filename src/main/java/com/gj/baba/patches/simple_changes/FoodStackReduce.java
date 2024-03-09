package com.gj.baba.patches.simple_changes;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class FoodStackReduce
{
    public static void Patch(FMLInitializationEvent event)
    {
        LivingDropsEvent a;
        for(Object it : Item.REGISTRY)
        {
            if(!(it instanceof ItemFood)) continue;

            ItemFood item = (ItemFood)it;
            int limit = item.getItemStackLimit();
            int heal_amount = item.getHealAmount(new ItemStack(item));
            if(limit > 7)
            {
                //itemstack limit divided by 8
                item.setMaxStackSize(limit >> 3);
            }
        }
    }
}
