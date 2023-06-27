package com.gj.bigbag.Items;

import com.google.common.collect.Multimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.EnumHelper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class RechargingMace extends BaseItemSword
{
    public RechargingMace (String name, Item.ToolMaterial material, CreativeTabs creativeTab)
    {
        super(name, material, creativeTab);

        this.setMaxDamage(255);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        if(!player.getCooldownTracker().hasCooldown(this) && entity.canBeAttackedWithItem())
        {
            player.getCooldownTracker().setCooldown(this, 120);
            return false;
        }

        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if(equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            replaceModifier(multimap, SharedMonsterAttributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER, 15);
            replaceModifier(multimap, SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, 100);
        }

        return multimap;
    }

    private void replaceModifier(Multimap<String, AttributeModifier> modifierMultimap, IAttribute attribute, UUID id, double value) {
        // Get the modifiers for the specified attribute
        final Collection<AttributeModifier> modifiers = modifierMultimap.get(attribute.getName());

        // Find the modifier with the specified ID, if any
        final Optional<AttributeModifier> modifierOptional = modifiers.stream().filter(attributeModifier -> attributeModifier.getID().equals(id)).findFirst();

        if (modifierOptional.isPresent()) { // If it exists,
            final AttributeModifier modifier = modifierOptional.get();
            modifiers.remove(modifier); // Remove it
            modifiers.add(new AttributeModifier(modifier.getID(), modifier.getName(), value, modifier.getOperation())); // Add the new modifier
        }
    }

    @Override
    public float getAttackDamage()
    {
        return 15f;
    }
}
