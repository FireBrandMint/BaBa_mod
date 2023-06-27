package com.gj.bigbag.Items;

import com.gj.bigbag.Items.util.StringIntContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class SatchelSeeds extends Satchel
{
    public SatchelSeeds (String name, CreativeTabs creativeTab)
    {
        super(name, creativeTab);
    }

    @Override
    public boolean InternalItemFitsInSatchel(ItemStack itemstack, StringIntContainer name)
    {
        String str = name.str;
        return str.endsWith("seeds") || str.endsWith("seed") || str.endsWith("sapling");
    }
}
