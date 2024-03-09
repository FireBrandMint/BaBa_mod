package com.gj.baba.patches.mixins;

import net.minecraft.advancements.critereon.VillagerTradeTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

@Mixin(WorldServer.class)
public abstract class PatchSleep extends World
{
    protected PatchSleep(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Shadow public abstract boolean areAllPlayersAsleep();

    @Shadow protected abstract void wakeAllPlayers();

    //Makes it so any player anywhere
    //can wake up regardless
    //if all players are sleeping or not.
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        if(this.isRemote) return;

        //Originally nonexistent, this 'if'
        //serves to fix a mod bug where if all
        //players sleep at the same tick,
        //time skips.
        if (this.areAllPlayersAsleep())
        {
            for (EntityPlayer entityplayer : this.playerEntities.stream().filter(EntityPlayer::isPlayerSleeping).collect(Collectors.toList()))
            {
                entityplayer.wakeUpPlayer(false, false, true);
                healOnWake(entityplayer);
            }

            this.wakeAllPlayers();
        }
        else
        {
            for (EntityPlayer entityplayer : this.playerEntities)
            {
                if (!entityplayer.isSpectator() && entityplayer.isPlayerFullyAsleep())
                {
                    entityplayer.wakeUpPlayer(false, false, true);
                    healOnWake(entityplayer);
                }
            }
        }
    }

    public void healOnWake(EntityPlayer entityplayer)
    {
        if(entityplayer.shouldHeal())
            entityplayer.heal(Math.min(8, entityplayer.getMaxHealth() - entityplayer.getHealth()));
    }
}
