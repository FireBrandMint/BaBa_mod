package com.gj.baba.potions;

import com.gj.baba.BaBa;
import com.gj.baba.init.PotionInit;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class StaminaLoss extends BasePotion
{
    public StaminaLoss(String name, boolean isBadEffectIn, int liquidColorIn, int iconIndexX, int iconIndexY)
    {
        super(name, isBadEffectIn, liquidColorIn, iconIndexX, iconIndexY);
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);

        /*
        if(entityLivingBaseIn.isPotionActive(PotionInit.ANTI_STUN))
        {
            entityLivingBaseIn.removeActivePotionEffect(PotionInit.STAMINA_LOSS);

            return;
        }
        if(amplifier >= 8)
        {
            entityLivingBaseIn.removeActivePotionEffect(PotionInit.STAMINA_LOSS);
            entityLivingBaseIn.addPotionEffect(new PotionEffect(PotionInit.STUN, 8));
        }
         */
    }
}
