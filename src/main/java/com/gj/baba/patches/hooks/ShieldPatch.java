package com.gj.baba.patches.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class ShieldPatch
{
    static String shieldCooldownTag = "ShieldBlockCooldown";
    static String shieldBlockTick = "ShieldBlockTick";
    static Random blockingRandom = new Random(System.nanoTime());

    static EntityEquipmentSlot currentShieldSlot;

    public static int enabled = 1;

    private static int getDefaultShieldCooldown(ItemStack shieldItem)
    {
        return 3;
    }

    //ItemShield calls this instead of the onItemRightClick from vanilla shield.
    //(ASMTransformer injects it)
    public static ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if(enabled == 0)
        {
            ItemStack lvt_4_1_ = playerIn.getHeldItem(handIn);
            playerIn.setActiveHand(handIn);
            return new ActionResult(EnumActionResult.SUCCESS, lvt_4_1_);
        }
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START) return;
        //reset shield after block
        if(enabled == 0) return;

        EntityPlayer player = event.player;
        ItemStack active = player.getActiveItemStack();
        if(!active.isEmpty() && active.getItem() instanceof ItemShield)
        {
            player.resetActiveHand();
        }
    }

    //to makeup for lost shield block, now it has a chance block
    public static void onDamageLiving(LivingAttackEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        DamageSource source = event.getSource();

        if(entity.world.isRemote | enabled == 0) return;

        if (entity.getHealth() <= 0.0F || (source.isFireDamage() && entity.isPotionActive(MobEffects.FIRE_RESISTANCE)))
        {
            return;
        }

        if(canBlockDamageSource(entity, source))
        {
            if((float)entity.hurtResistantTime > (float)entity.maxHurtResistantTime * 0.5f)
            {
                return;
            }

            ItemStack shield = entity.getItemStackFromSlot(currentShieldSlot);
            NBTTagCompound compound;
            if(shield.hasTagCompound())
            {
                compound = shield.getTagCompound();
            }
            else
            {
                compound = new NBTTagCompound();
                shield.setTagCompound(compound);
            }
            boolean skipOperation = true;

            int cooldown = 0;

            int defaultCooldown = getDefaultShieldCooldown(shield);
            if(compound.hasKey(shieldCooldownTag))
            {
                cooldown = compound.getInteger(shieldCooldownTag) - 1;
                if (cooldown < 0)
                {
                    cooldown = defaultCooldown + blockingRandom.nextInt(2);
                    compound.setInteger(shieldBlockTick, blockingRandom.nextInt(defaultCooldown));
                }
                if(cooldown == compound.getInteger(shieldBlockTick)) skipOperation = false;

                compound.setInteger(shieldCooldownTag, cooldown);
            }
            else
            {
                compound.setInteger(shieldBlockTick, blockingRandom.nextInt(defaultCooldown));
                compound.setInteger(shieldCooldownTag, defaultCooldown);
                skipOperation = false;
            }

            if(skipOperation)
            {
                //if(entity instanceof EntityPlayer) ((EntityPlayer) entity).sendMessage(new TextComponentString("failed" + cooldown));
                return;
            }
            else
            {
                //if(entity instanceof EntityPlayer) ((EntityPlayer) entity).sendMessage(new TextComponentString("success" + cooldown));
            }

            entity.hurtResistantTime = entity.maxHurtResistantTime;

            if (!source.isProjectile())
            {
                shield.damageItem(Math.max(1, (int)(event.getAmount() * 2f)), entity);
                entity.setItemStackToSlot(currentShieldSlot, shield);

                Entity immediateSource = source.getImmediateSource();

                if (immediateSource != null && immediateSource instanceof EntityLivingBase)
                {
                    entity.setActiveHand(currentShieldSlot == EntityEquipmentSlot.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                    blockUsingShield(entity, (EntityLivingBase)immediateSource);
                }
            }

            if(entity instanceof EntityPlayer)
            {
                entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
                        SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1f, 1f);
            }
            else entity.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1f, 1f);

            event.setCanceled(true);
        }
    }

    static void blockUsingShield(EntityLivingBase entity, EntityLivingBase p_190629_1_)
    {
        p_190629_1_.knockBack(entity, 0.5F, entity.posX - p_190629_1_.posX, entity.posZ - p_190629_1_.posZ);
    }

    private static boolean canBlockDamageSource(EntityLivingBase entity, DamageSource damageSourceIn)
    {
        if(damageSourceIn.isUnblockable()) return false;

        Item off = (entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND)).getItem();
        Item main = (entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND)).getItem();
        ItemStack stack;

        boolean shieldFound = false;

        if(off instanceof ItemShield)
        {
            currentShieldSlot = EntityEquipmentSlot.OFFHAND;
            shieldFound = true;
        }
        else if (main instanceof ItemShield)
        {
            currentShieldSlot = EntityEquipmentSlot.MAINHAND;
            shieldFound = true;
        }

        if (shieldFound)
        {
            Vec3d vec3d = damageSourceIn.getDamageLocation();

            if (vec3d != null)
            {
                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(entity.posX, entity.posY, entity.posZ)).normalize();
                vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);

                if (vec3d2.dotProduct(vec3d1) < 0.0D)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
