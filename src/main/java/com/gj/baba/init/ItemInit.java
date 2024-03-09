package com.gj.baba.init;

import com.gj.baba.Items.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemInit
{
    public static List<Item> ITEMS;
    public static Item MACE_RECHARGING;
    public static Item BSPIKE_IMAGE;
    public static Item REQUIEM_BOVID;
    public static Item REQUIEM_LIFE;

    public static Item TEST_STUN_PROD;

    public static void Initialize()
    {
        if (ITEMS == null) ITEMS = new ArrayList<Item>();
        //ITEM_TEST = new BaseItem("test_item", C);
        //ITEM_SATCHEL_MINERAL = new SatchelMinerals("ore_satchel", CreativeTabs.TOOLS);
        //ITEM_SATCHEL_FOOD = new SatchelFood("food_satchel", CreativeTabs.TOOLS);
        //ITEM_SATCHEL_SEEDS = new SatchelSeeds("seed_satchel", CreativeTabs.TOOLS);
        MACE_RECHARGING = new RechargingMace("recharging_mace", Item.ToolMaterial.DIAMOND, CreativeTabs.COMBAT);
        BSPIKE_IMAGE = new BSpikeImage("bspike_image", null);
        REQUIEM_BOVID = new RequiemBovid("requiem_bovid", CreativeTabs.MISC);
        REQUIEM_LIFE = new RequiemLife("requiem_life", CreativeTabs.COMBAT);
        TEST_STUN_PROD = new StunProd("stunprod", CreativeTabs.COMBAT);
    }
}
