package com.gj.baba.Items;

import com.gj.baba.Items.util.ItemUtils;
import com.gj.baba.potions.Stun;
import com.google.common.collect.Multimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TestStunProd extends BaseItemSword
{
    public TestStunProd (String name, Item.ToolMaterial material, CreativeTabs creativeTab)
    {
        super(name, material, creativeTab);

        this.setMaxDamage(255);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        if(!(entity instanceof EntityLivingBase)) return true;

        Stun.tryStunTarget((EntityLivingBase)entity, 4, 200, 400);

        return false;
    }

    @Override
    public float getAttackDamage()
    {
        return 1f;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if(equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            ItemUtils.replaceModifier(multimap, SharedMonsterAttributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER, 0d);
            ItemUtils.replaceModifier(multimap, SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, 1);
        }

        return multimap;
    }
}
