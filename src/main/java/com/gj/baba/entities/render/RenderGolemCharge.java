package com.gj.baba.entities.render;

import com.gj.baba.BaBa;
import com.gj.baba.entities.entity.BulletSpike;
import com.gj.baba.entities.entity.EntityGolemCharge;
import com.gj.baba.entities.models.ModelGolemCharge;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

public class RenderGolemCharge extends RenderLiving<EntityGolemCharge>
{
    public static ResourceLocation TEXTURE = null;

    public RenderGolemCharge(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
        if(TEXTURE == null) TEXTURE = new ResourceLocation(BaBa.ModId, "textures/entity/golem_charge.png");
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGolemCharge entity) {
        return TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityGolemCharge>
    {

        @Override
        public Render<? super EntityGolemCharge> createRenderFor(RenderManager manager) {
            return new RenderGolemCharge(manager, new ModelGolemCharge(), 0.3f);
        }
    }
}
