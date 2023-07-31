package com.gj.baba.potions;

import com.gj.baba.BaBa;
import com.gj.baba.init.PotionInit;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class Stun extends BasePotion {

    public static boolean tryStunTarget(EntityLivingBase target, int hitsTillStun, int stunTime, int stunResistanceOnStun)
    {
        if(target.isPotionActive(PotionInit.ANTI_STUN)) return false;

        if(target.isPotionActive(PotionInit.STAMINA_LOSS))
        {
            int amp = target.getActivePotionEffect(PotionInit.STAMINA_LOSS).getAmplifier();
            target.removePotionEffect(PotionInit.STAMINA_LOSS);

            if(amp >= hitsTillStun - 1)
            {
                target.addPotionEffect(new PotionEffect(PotionInit.STUN, stunTime));
                if(stunResistanceOnStun != 0)target.addPotionEffect(new PotionEffect(PotionInit.ANTI_STUN, stunResistanceOnStun));
            }
            else
            {
                target.addPotionEffect(new PotionEffect(PotionInit.STAMINA_LOSS, 100, amp + 1));
            }
        }
        else
        {
            target.addPotionEffect(new PotionEffect(PotionInit.STAMINA_LOSS, 100));
        }

        return true;
    }

    static ResourceLocation textures = null;

    public Stun(String name, boolean isBadEffectIn, int liquidColorIn, int iconIndexX, int iconIndexY)
    {
        super(name, isBadEffectIn, liquidColorIn, iconIndexX, iconIndexY);

        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "be92fe39-9fd3-4dbd-9d2d-b2ebbbc82f1b", -1f, 2);
        //this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "665dae5d-4033-4183-bbf6-3c8106cac552", -1f, 0);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "4774bdb7-6dd6-4ad8-b961-954c3e3c1820", -1f, 2);

    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        //super.performEffect(entityLivingBaseIn, amplifier);
        /*
        if(!entityLivingBaseIn.world.isRemote && entityLivingBaseIn.motionY > 0f)
        {
            entityLivingBaseIn.motionY = 0f;
            entityLivingBaseIn.isAirBorne = false;
            entityLivingBaseIn.setLocationAndAngles(entityLivingBaseIn.posX, entityLivingBaseIn.posY, entityLivingBaseIn.posZ);
        }
         */
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);

        /*if(entityLivingBaseIn instanceof EntityLiving)
        {
            EntityLiving living = ((EntityLiving) entityLivingBaseIn);

            if(!living.isAIDisabled()) living.setNoAI(true);
        }

         */

    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);


        /*if(entityLivingBaseIn instanceof EntityLiving)
        {
            EntityLiving living = ((EntityLiving) entityLivingBaseIn);

            living.setNoAI(false);
        }

         */


    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean hasStatusIcon() {
        if(textures == null) textures = new ResourceLocation(BaBa.ModId, "textures/gui/potion_effects.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
        return super.hasStatusIcon();
    }
}
