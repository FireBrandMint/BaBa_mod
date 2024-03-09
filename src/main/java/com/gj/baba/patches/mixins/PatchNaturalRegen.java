package com.gj.baba.patches.mixins;

import com.gj.baba.capabilities.StatsSystem;
import com.gj.baba.patches.util.NBTOfPatches;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(FoodStats.class)
public abstract class PatchNaturalRegen
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

        int armor = player.getTotalArmorValue();
        if(player.isSprinting())
        {
            this.foodExhaustionLevel += 0.01f;
            if(player.getHealth() < player.getMaxHealth() * 0.55) this.foodExhaustionLevel += 0.03f;
            if(armor > 5) this.foodExhaustionLevel += Math.min(0.066f * (armor * 0.05f), 0.066f);
        }

        if (this.foodLevel <= 6) this.foodExhaustionLevel += 0.04f;

        if (this.foodExhaustionLevel > 4.0F)
        {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                float decay = player.getCapability(StatsSystem.CAPABILITY, StatsSystem.sideFinal)
                        .applyStatModifiers(1f, StatsSystem.EnumStats.SATURATION_DEPLETION);
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - decay, 0.0F);
            }
            else if (enumdifficulty != EnumDifficulty.PEACEFUL)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }

            if (this.foodLevel <= 0 && enumdifficulty != EnumDifficulty.PEACEFUL)
                player.attackEntityFrom(DamageSource.STARVE, 2f);
        }

        FoodStats stats = player.getFoodStats();
        if(stats.getFoodLevel() > 10 && player.shouldHeal())
        {
            if(foodTimer <= 0)
            {
                if(armor > 12) this.foodExhaustionLevel += 1f;
                if(armor > 15) this.foodExhaustionLevel += 1f;
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

                int multiplier = (20 - this.foodLevel) / 3;
                if(multiplier <= 0) multiplier = 1;
                cooldown = Math.max(60, cooldown) * multiplier;

                this.foodTimer = cooldown;
            }

            --this.foodTimer;
        }

        if(stats.getFoodLevel() < 7)
        {
            Potion effect = Potion.getPotionFromResourceLocation("slowness");
            PotionEffect active = player.getActivePotionEffect(effect);
            PotionEffect pe;
            if(active == null)
            {
                pe = new PotionEffect(effect, 200, 0, false, false);
                player.addPotionEffect(pe);
            }
            else if(active.getAmplifier() < 1 && active.getDuration() < 100)
            {
                player.removePotionEffect(effect);
                pe = new PotionEffect(effect, 200, 0, false, false);
                player.addPotionEffect(pe);
            }

            if (foodLevel != 0) foodLevel += 0.03f;
        }
    }
}
