package com.gj.baba.potions;

import com.gj.baba.BaBa;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class BasePotion extends Potion
{
    public BasePotion(String name, boolean isBadEffectIn, int liquidColorIn, int iconIndexX, int iconIndexY)
    {
        super(isBadEffectIn, liquidColorIn);

        setPotionName("effect." + name);
        setRegistryName(new ResourceLocation(BaBa.ModId, name));
        setIconIndex(iconIndexX, iconIndexY);
    }
}
