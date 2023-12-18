package com.gj.baba.init;

import com.gj.baba.BaBa;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundInit
{
    public static SoundEvent STUN_DEFAULT;

    public static void Initialize()
    {
        STUN_DEFAULT = registerSound("item.melee.stundefault");
    }

    private static SoundEvent registerSound(String name)
    {
        ResourceLocation location = new ResourceLocation(BaBa.ModId, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        return event;
    }
}
