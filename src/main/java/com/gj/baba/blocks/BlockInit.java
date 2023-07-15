package com.gj.baba.blocks;

import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockInit
{
    public static List<Block> BLOCKS;

    //public static Block BLOCK_TEST = new BlockBase("test_block", Material.IRON, CreativeTabs.SEARCH);

    public static void Initialize()
    {
        BLOCKS = new ArrayList<Block>();
    }
}
