package com.gj.baba.entities.render;

import com.gj.baba.BaBa;
import com.gj.baba.entities.entity.BulletSpike;
import com.gj.baba.entities.models.ModelBulletSpike;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

public class RenderBulletSpike extends RenderLiving<BulletSpike>
{

    public RenderBulletSpike(RenderManager manager)
    {
        super(manager, new ModelBulletSpike(), 0.5f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(BulletSpike entity)
    {
        return new ResourceLocation(BaBa.ModId, "textures/entity/bulletspike.png");
    }

    @Override
    protected void applyRotations(BulletSpike entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    }

    public static class Factory implements IRenderFactory<BulletSpike>
    {

        @Override
        public Render<? super BulletSpike> createRenderFor(RenderManager manager) {
            return new RenderBulletSpike(manager);
        }
    }
}
