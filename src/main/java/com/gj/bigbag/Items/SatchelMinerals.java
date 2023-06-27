package com.gj.bigbag.Items;

import com.gj.bigbag.Items.util.StringIntContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SatchelMinerals extends Satchel
{


    public SatchelMinerals (String name, CreativeTabs creativeTab)
    {
        super(name, creativeTab);
    }

    @Override
    public boolean InternalItemFitsInSatchel(ItemStack itemstack, StringIntContainer name)
    {
        return name.str.endsWith("ore") || name.str.endsWith("dust");
    }


}
