package com.gj.baba.init;

import com.gj.baba.BaBa;
import com.gj.baba.blocks.BlockGasses;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockInit
{
    public static List<Block> BLOCKS;

    //public static Block BLOCK_TEST = new BlockBase("test_block", Material.IRON, CreativeTabs.SEARCH);
    public static Block BLOCK_GAS;

    public static void Initialize()
    {
        if(ItemInit.ITEMS == null) ItemInit.ITEMS = new ArrayList<Item>();
        BLOCKS = new ArrayList<Block>();
        BLOCK_GAS = new BlockGasses("gasses_block", Material.IRON, CreativeTabs.BREWING);
    }

    public static void InitTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntity.class, new ResourceLocation(BaBa.ModId, "tile_entity_gas"));
    }
}
