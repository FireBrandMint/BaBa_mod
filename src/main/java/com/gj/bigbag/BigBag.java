package com.gj.bigbag;

import com.gj.bigbag.Items.IHasModel;
import com.gj.bigbag.blocks.BlockInit;
import com.gj.bigbag.init.ItemInit;
import com.gj.bigbag.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.stats.StatBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
@Mod(modid = BigBag.ModId, name = BigBag.Name, version = BigBag.Version)
public class BigBag
{
    public static final String ModId = "bigbag";
    public static final String Name = "BigBag mod";

    public static final String Version = "0.0.1";
    public static final String Common = "com.gj.bigbag.proxy.CommonProxy";
    public static final String Client = "com.gj.bigbag.proxy.ClientProxy";

    @Mod.Instance
    public static BigBag Instance;

    @SidedProxy(clientSide = BigBag.Client, serverSide = BigBag.Common)
    public static CommonProxy Proxy;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {

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
