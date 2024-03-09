package com.gj.baba.init;

import com.gj.baba.patches.ASMTransformer;
import com.gj.baba.patches.simple_changes.FoodStackReduce;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

//for altering vanilla
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("whatever")
public class PatchInit implements IFMLLoadingPlugin {
    public PatchInit()
    {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.baba.json");
    }

    public static void Init(FMLInitializationEvent event)
    {
        FoodStackReduce.Patch(event);
    }
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {ASMTransformer.class.getTypeName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        //This will return the jar file of this mod's .jar"
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}