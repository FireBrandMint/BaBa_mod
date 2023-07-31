package com.gj.baba.init;

import com.gj.baba.potions.AntiStun;
import com.gj.baba.potions.Stun;
import com.gj.baba.potions.StaminaLoss;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PotionInit
{
    public static Stun STUN = new Stun("stun", true, 0x919191, 0, 0);
    public static StaminaLoss STAMINA_LOSS = new StaminaLoss("stamina_lost", true, 0xB2DBFF, 0, 0);
    public static AntiStun ANTI_STUN = new AntiStun("anti_stun", false, 0xF4FFA8, 1, 0);
    public static void Init()
    {
        RegisterEffect(STUN);
        RegisterEffect(STAMINA_LOSS);
        RegisterEffect(ANTI_STUN);
    }

    static void RegisterEffect(Potion effect)
    {
        ForgeRegistries.POTIONS.register(effect);
    }
}
