package com.gj.baba.init;

import com.gj.baba.BaBa;
import com.gj.baba.capabilities.GasSystem;
import com.gj.baba.capabilities.StatsSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CapabilityInit
{
    public static ResourceLocation STAT_CAP_NAME = null;
    public static void Initialize()
    {
        //part of the forgotten gas system
        //CapabilityManager.INSTANCE.register(GasSystem.IGasMatrix.class, new GasSystem.GasStorage(), new GasSystem.PolutionFactory());
        CapabilityManager.INSTANCE.register(StatsSystem.IBabaStats.class, new StatsSystem.Storage(), new StatsSystem.Factory());
    }

    public static void AttachEntities(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getObject();
            if(!player.hasCapability(StatsSystem.CAPABILITY, EnumFacing.EAST))
            {
                if(STAT_CAP_NAME == null) STAT_CAP_NAME = new ResourceLocation(BaBa.ModId, "babaplayerstats");
                event.addCapability(STAT_CAP_NAME, new StatsSystem.Provider());
            }
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if(player.world.isRemote) return;
        if(event.phase == TickEvent.Phase.END)
        {
            if(player.hasCapability(StatsSystem.CAPABILITY, StatsSystem.sideFinal))
            {
                StatsSystem.IBabaStats stats = player.getCapability(StatsSystem.CAPABILITY, StatsSystem.sideFinal);
                stats.resetStatModifiers();
                stats.resetTempFlags();
            }
        }
    }
}
