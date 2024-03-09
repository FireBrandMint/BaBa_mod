package com.gj.baba;

import com.gj.baba.Items.IHasModel;
import com.gj.baba.patches.hooks.ShieldPatch;
import com.gj.baba.components.substances.Substance;
import com.gj.baba.init.*;
import com.gj.baba.patches.simple_changes.OPFoodDropLow;
import com.gj.baba.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = BaBa.ModId, name = BaBa.Name, version = BaBa.Version)
public class BaBa
{
    public static final String ModId = "baba";
    public static final String Name = "BaBa mod";

    public static final String Version = "0.0.1";
    public static final String Common = "com.gj.baba.proxy.CommonProxy";
    public static final String Client = "com.gj.baba.proxy.ClientProxy";
    public static Logger logger = LogManager.getLogger(BaBa.ModId);

    @Mod.Instance
    public static BaBa Instance;

    @SidedProxy(clientSide = BaBa.Client, serverSide = BaBa.Common)
    public static CommonProxy Proxy;

    public static int CurrSessionTick = 0;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {
        EntityInit.Initialize();
        BlockInit.InitTileEntities();
        Proxy.InitializeEntityRenderer();

        PotionInit.Init();
        SoundInit.Initialize();
    }

    @Mod.EventHandler
    public static void Init(FMLInitializationEvent event)
    {
        CapabilityInit.Initialize();
        Substance.InitializeSubstances();
        PatchInit.Init(event);
    }

    @Mod.EventHandler
    public static void PostInit(FMLPostInitializationEvent event)
    {
        //Biomes.FOREST.decorator.treesPerChunk = 2;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void OnAttachChunkCapabilities(AttachCapabilitiesEvent<Chunk> event)
    {
        //GasSystem.OnAttachChunkCapabilities(event);
    }
    @SubscribeEvent
    public static void OnAttachEntityCapability(AttachCapabilitiesEvent<Entity> event)
    {
        CapabilityInit.AttachEntities(event);
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void movementInputUpdate(InputUpdateEvent event)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null && !player.isDead) {
            if (player.isPotionActive(PotionInit.STUN)) {

                event.getMovementInput().forwardKeyDown = false;
                event.getMovementInput().backKeyDown = false;
                event.getMovementInput().leftKeyDown = false;
                event.getMovementInput().rightKeyDown = false;
                event.getMovementInput().moveForward = 0f;
                event.getMovementInput().moveStrafe = 0f;
                event.getMovementInput().jump = false;
            }
        }
    }
    @SubscribeEvent(receiveCanceled = true)
    public static void onMouseInputEvent(InputEvent.MouseInputEvent event)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player == null || !player.isPotionActive(PotionInit.STUN)) return;

        GameSettings gs = Minecraft.getMinecraft().gameSettings;

        if (gs.keyBindAttack.isPressed()) // add your additional conditions here
        {
            KeyBinding.setKeyBindState(gs.keyBindAttack.getKeyCode(), false);
        }

        if(gs.keyBindUseItem.isPressed())
        {
            KeyBinding.setKeyBindState(gs.keyBindUseItem.getKeyCode(), false);
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        ShieldPatch.onPlayerTick(event);
        CapabilityInit.onPlayerTick(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event)
    {
        DamageSource source = event.getSource();

        Entity trueSource = source.getTrueSource();

        if(trueSource != null && trueSource instanceof EntityLivingBase)
        {
            if(((EntityLivingBase) trueSource).isPotionActive(PotionInit.STUN))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void Patch(LivingDropsEvent event)
    {
        OPFoodDropLow.Patch(event);
    }

    @Mod.EventHandler
    public static void onStart(FMLServerStartedEvent event)
    {
        CurrSessionTick = 0;
    }

    @Mod.EventHandler
    public static void onServerShutdown(FMLServerStoppedEvent event)
    {

    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        //GasSystem.OnServerTick(event);
    }

    @SubscribeEvent
    public static void onWorldTick (TickEvent.WorldTickEvent event)
    {
        if(event.side == Side.CLIENT) return;

        if(event.phase == TickEvent.Phase.START)
        {
            ++CurrSessionTick;
        }
    }
    @SubscribeEvent
    public static void onDamageLiving(LivingAttackEvent event)
    {
        ShieldPatch.onDamageLiving(event);
    }

    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event)
    {
        if(event.getWorld().isRemote) return;
        //GasSystem.OnChunkLoad(event);
    }

    @SubscribeEvent
    public static void chunkSave(ChunkEvent.Save event)
    {

    }

    @SubscribeEvent
    public static void chunkUnload(ChunkEvent.Unload event)
    {
        if(event.getWorld().isRemote) return;
        //GasSystem.OnChunkUnload(event);
    }
}
