package com.gj.baba.init;

import com.gj.baba.capabilities.GasSystem;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInit
{
    public static void Initialize()
    {
        //part of the forgotten gas system
        //CapabilityManager.INSTANCE.register(GasSystem.IGasMatrix.class, new GasSystem.GasStorage(), new GasSystem.PolutionFactory());

    }
}
