package com.gj.baba.init;

import com.gj.baba.BaBa;
import com.gj.baba.entities.entity.BulletSpike;
import com.gj.baba.entities.models.ModelBulletSpike;
import com.gj.baba.entities.render.RenderBulletSpike;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit
{
    public static int bulletSpikeID = 87000;

    public static void Initialize()
    {

        RegisterEntity("bullet_spike", BulletSpike.class, bulletSpikeID, 50, 0x5BBCF4, 0x43E88D);
    }

    public static void RegisterRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(BulletSpike.class, new RenderBulletSpike.Factory());
    }

    private static void RegisterEntity(String name, Class<? extends Entity> entity, int id, int range, int eggColor1, int eggColor2)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(BaBa.ModId + ":" + name), entity, name, id, BaBa.Instance, range, 1, true, eggColor1, eggColor2);
    }
}
