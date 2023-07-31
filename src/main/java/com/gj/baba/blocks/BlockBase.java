package com.gj.baba.blocks;

import com.gj.baba.BaBa;
import com.gj.baba.Items.IHasModel;
import com.gj.baba.init.BlockInit;
import com.gj.baba.init.ItemInit;
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
