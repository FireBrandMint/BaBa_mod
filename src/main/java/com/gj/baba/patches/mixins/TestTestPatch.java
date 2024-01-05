package com.gj.baba.patches.mixins;

import com.gj.baba.BaBa;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class TestTestPatch
{
    @Inject(method = {"onUpdate"}, at = {@At("HEAD")})
    public void onUpdate(CallbackInfo ci)
    {
        BaBa.logger.atInfo().log("worked");
    }
}
