package com.gj.bigbag.Items;

import com.gj.bigbag.Items.util.StringIntContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class SatchelFood extends Satchel
{
    public SatchelFood (String name, CreativeTabs creativeTab)
    {
        super(name, creativeTab);
    }

    @Override
    public boolean InternalItemFitsInSatchel(ItemStack itemstack, StringIntContainer name)
    {
        return itemstack.getItem() instanceof ItemFood;
    }
}
