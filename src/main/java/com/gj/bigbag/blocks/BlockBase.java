package com.gj.bigbag.blocks;

import com.gj.bigbag.BaBa;
import com.gj.bigbag.Items.IHasModel;
import com.gj.bigbag.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel
{
    public BlockBase(String name, Material material, CreativeTabs tab)
    {
        super(material);

        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(tab);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void RegisterModels()
    {
        BaBa.Proxy.RegisterItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
