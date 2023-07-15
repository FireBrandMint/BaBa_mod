package com.gj.baba;

import com.gj.baba.Items.IHasModel;
import com.gj.baba.blocks.BlockInit;
import com.gj.baba.init.EntityInit;
import com.gj.baba.init.ItemInit;
import com.gj.baba.proxy.ClientProxy;
import com.gj.baba.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = BaBa.ModId, name = BaBa.Name, version = BaBa.Version)
public class BaBa
{
    public static final String ModId = "baba";
    public static final String Name = "BaBa mod";

    public static final String Version = "0.0.1";
    public static final String Common = "com.gj.baba.proxy.CommonProxy";
    public static final String Client = "com.gj.baba.proxy.ClientProxy";

    @Mod.Instance
    public static BaBa Instance;

    @SidedProxy(clientSide = BaBa.Client, serverSide = BaBa.Common)
    public static CommonProxy Proxy;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {
        EntityInit.Initialize();
        Proxy.InitializeEntityRenderer();
    }

    @Mod.EventHandler
    public static void Init(FMLInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public static void PostInit(FMLPostInitializationEvent event)
    {

    }

    @SubscribeEvent
    public static void OnItemRegister(RegistryEvent.Register<Item> event)
    {
        ItemInit.Initialize();
        event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void OnBlockRegister(RegistryEvent.Register<Block> event)
    {
        BlockInit.Initialize();
        event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void OnModelRegister(ModelRegistryEvent event)
    {
        for(Item item : ItemInit.ITEMS)
        {
            if(item instanceof IHasModel)
            {
                ((IHasModel) item).RegisterModels();
            }
        }

        for(Block block : BlockInit.BLOCKS)
        {
            if(block instanceof IHasModel)
            {
                ((IHasModel) block).RegisterModels();
            }
        }
    }
}
