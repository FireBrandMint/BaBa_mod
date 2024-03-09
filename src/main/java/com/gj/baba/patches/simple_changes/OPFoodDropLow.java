package com.gj.baba.patches.simple_changes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;


public class OPFoodDropLow
{
    private static Random rand = new Random(System.nanoTime());
    public static void Patch(LivingDropsEvent event)
    {
        //TODO: Make op food from drops
        //TODO: get deleted with a chance
        //TODO: unless it has potion effects.
        EntityLivingBase entity = event.getEntityLiving();

        if(entity instanceof EntityPlayer || entity.getMaxHealth() > 79f) return;

        boolean one_food = false;

        List<EntityItem> old_drops = event.getDrops();
        for(int i = 0; i < old_drops.size(); ++i)
        {
            EntityItem curr = old_drops.get(i);
            ItemStack curr_stack = curr.getItem();
            Item curr_item = curr_stack.getItem();
            if(curr_item instanceof ItemFood)
            {
                if(!one_food)
                {
                    if(rand.nextInt(6) == 0) return;
                    one_food = true;
                }

                ItemFood curr_food = (ItemFood) curr_item;
                if(curr_food.getHealAmount(curr_stack) >= 7)
                {
                    curr.setDead();
                    continue;
                }
                ItemStack curr_smelt = FurnaceRecipes.instance().getSmeltingResult(curr_stack);
                if
                (
                        !curr_smelt.isEmpty() &&
                        curr_smelt.getItem() instanceof ItemFood &&
                        ((ItemFood) curr_smelt.getItem()).getHealAmount(curr_smelt) >=7
                )
                    curr.setDead();

            }
        }
    }
}
