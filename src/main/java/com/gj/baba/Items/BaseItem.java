package com.gj.baba.Items;

import com.gj.baba.BaBa;
import com.gj.baba.init.ItemInit;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class BaseItem extends Item implements IHasModel
{
    public BaseItem(String name, CreativeTabs creativeTab)
    {
        super();

        setRegistryName(new ResourceLocation(BaBa.ModId, name));
        setUnlocalizedName(BaBa.ModId + "." + name);

        if(creativeTab != null) setCreativeTab(creativeTab);

        ItemInit.ITEMS.add(this);
    }

    public void RegisterModels()
    {
        ResourceLocation resourceLocation = this.getRegistryName();

        if (resourceLocation != null)
        {
            ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(resourceLocation, "inventory"));
        }
    }
}
