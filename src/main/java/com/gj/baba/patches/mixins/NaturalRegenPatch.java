package com.gj.baba.patches.mixins;

import com.gj.baba.blocks.util.ExtractionChestInventory;
import com.gj.baba.patches.util.NBTOfPatches;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(FoodStats.class)
public abstract class NaturalRegenPatch
{
    @Shadow private float foodExhaustionLevel;

    @Shadow private float foodSaturationLevel;

    @Shadow private int foodLevel;

    @Shadow private int prevFoodLevel;

    @Shadow private int foodTimer;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdate(EntityPlayer player)
    {
        EnumDifficulty enumdifficulty = player.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 4.0F)
        {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (enumdifficulty != EnumDifficulty.PEACEFUL)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        FoodStats stats = player.getFoodStats();
        if(stats.getFoodLevel() > 17 && player.shouldHeal())
        {


            if(foodTimer <= 0)
            {

                player.heal(1f);

                int cooldown = 0;

                NBTTagCompound playerData = player.getEntityData();
                if(!playerData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
                NBTTagCompound persistent = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

                if(persistent.hasKey(NBTOfPatches.HEALING_COOLDOWN_TAG))
                {
                    cooldown = persistent.getInteger(NBTOfPatches.HEALING_COOLDOWN_TAG);
                }
                else cooldown = 100 - (player.experienceLevel >> 1);

                cooldown = Math.max(20, cooldown);

                this.foodTimer = cooldown;
            }

            --this.foodTimer;
        }
    }
}
