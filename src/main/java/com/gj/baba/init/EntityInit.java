package com.gj.baba.init;

import com.gj.baba.BaBa;
import com.gj.baba.entities.entity.BulletSpike;
import com.gj.baba.entities.entity.EntityGolemCharge;
import com.gj.baba.entities.models.ModelBulletSpike;
import com.gj.baba.entities.render.RenderBulletSpike;
import com.gj.baba.entities.render.RenderGolemCharge;
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
    public static int golemChargeID = 87001;

    public static void Initialize()
    {
        RegisterEntity("bullet_spike", BulletSpike.class, bulletSpikeID, 50);
        RegisterEntity("golem_charge", EntityGolemCharge.class, golemChargeID, 50, 0x666565, 0x9B3D2F);
    }

    public static void RegisterRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(BulletSpike.class, new RenderBulletSpike.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemCharge.class, new RenderGolemCharge.Factory());
    }

    private static void RegisterEntity(String name, Class<? extends Entity> entity, int id, int range)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(BaBa.ModId + ":" + name), entity, name, id, BaBa.Instance, range, 1, true);
    }

    private static void RegisterEntity(String name, Class<? extends Entity> entity, int id, int range, int eggColor1, int eggColor2)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(BaBa.ModId + ":" + name), entity, name, id, BaBa.Instance, range, 1, true, eggColor1, eggColor2);
    }
}
