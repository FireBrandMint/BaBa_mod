package com.gj.baba.Items;

import com.gj.baba.BaBa;
import com.gj.baba.init.ItemInit;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class BaseItemFood extends ItemFood implements IHasModel
{

    public BaseItemFood(String name, CreativeTabs creativeTab, int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, isWolfFood);

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
