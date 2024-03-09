package com.gj.baba.patches.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;

public class PatchObjectHolder
{
    static Enchantment[][] mendingSubstitutes = null;

    public static Enchantment[][] getMendingSubstitutes()
    {
        if(mendingSubstitutes == null)
        {
            mendingSubstitutes = new Enchantment[][] {
                    new Enchantment[] {
                            Enchantments.UNBREAKING,
                            Enchantments.SILK_TOUCH,
                            Enchantments.EFFICIENCY
                    },
                    new Enchantment[] {
                            Enchantments.FLAME,
                            Enchantments.PUNCH,
                            Enchantments.INFINITY
                    },
                    new Enchantment[] {
                            Enchantments.LURE,
                            Enchantments.LUCK_OF_THE_SEA,
                            Enchantments.UNBREAKING
                    }
            };
        }
        //if end
        return mendingSubstitutes;
    }
}
