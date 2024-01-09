package com.gj.baba.entities.models;

import com.gj.baba.entities.entity.EntityGolemCharge;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * ModelZombie - Either Mojang or a mod author
 * Created using Tabula 7.1.0
 */
public class ModelGolemCharge extends ModelBase {
    public ModelRenderer leg_right;
    public ModelRenderer head;
    public ModelRenderer torso;
    public ModelRenderer leg_left;
    public ModelRenderer sholder_left;
    public ModelRenderer sholder_right;
    public ModelRenderer screw_1;
    public ModelRenderer screw_2;
    public ModelRenderer arm_left_1;
    public ModelRenderer arm_left_2;
    public ModelRenderer arm_right_1;
    public ModelRenderer arm_right_2;

    public ModelGolemCharge() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.arm_left_2 = new ModelRenderer(this, 48, 23);
        this.arm_left_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.arm_left_2.addBox(-2.5F, 2.0F, -1.0F, 5, 7, 2, 0.0F);
        this.leg_right = new ModelRenderer(this, 0, 19);
        this.leg_right.setRotationPoint(-2.5F, 15.0F, -0.0F);
        this.leg_right.addBox(-2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F);
        this.torso = new ModelRenderer(this, 16, 16);
        this.torso.setRotationPoint(0.0F, 10.0F, 0.0F);
        this.torso.addBox(-5.0F, -5.0F, -3.0F, 10, 10, 6, 0.0F);
        this.leg_left = new ModelRenderer(this, 0, 19);
        this.leg_left.mirror = true;
        this.leg_left.setRotationPoint(2.5F, 15.0F, 0.0F);
        this.leg_left.addBox(-2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.arm_right_2 = new ModelRenderer(this, 48, 23);
        this.arm_right_2.mirror = true;
        this.arm_right_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arm_right_2.addBox(-2.5F, 0.0F, -1.0F, 5, 7, 2, 0.0F);
        this.screw_2 = new ModelRenderer(this, 32, 7);
        this.screw_2.setRotationPoint(-5.0F, 14.0F, 0.0F);
        this.screw_2.addBox(0.0F, 0.0F, 0.0F, 10, 1, 0, 0.0F);
        this.arm_left_1 = new ModelRenderer(this, 48, 32);
        this.arm_left_1.setRotationPoint(2.5F, 4.0F, 0.0F);
        this.arm_left_1.addBox(-1.0F, 0.0F, -2.0F, 2, 9, 4, 0.0F);
        this.sholder_left = new ModelRenderer(this, 43, 11);
        this.sholder_left.mirror = true;
        this.sholder_left.setRotationPoint(5.0F, 6.0F, 0.5F);
        this.sholder_left.addBox(0.0F, 0.0F, -2.5F, 5, 4, 5, 0.0F);
        this.sholder_right = new ModelRenderer(this, 43, 11);
        this.sholder_right.setRotationPoint(-5.0F, 6.0F, 0.0F);
        this.sholder_right.addBox(-5.0F, 0.0F, -2.5F, 5, 4, 5, 0.0F);
        this.screw_1 = new ModelRenderer(this, 32, 2);
        this.screw_1.setRotationPoint(-10.0F, -5.0F, 0.0F);
        this.screw_1.addBox(-5.0F, 0.0F, 0.0F, 10, 6, 0, 0.0F);
        this.setRotateAngle(screw_1, 0.0F, 0.0F, -1.5707963267948966F);
        this.arm_right_1 = new ModelRenderer(this, 48, 32);
        this.arm_right_1.setRotationPoint(-2.5F, 4.0F, 0.0F);
        this.arm_right_1.addBox(-1.0F, 0.0F, -2.0F, 2, 9, 4, 0.0F);
        this.arm_left_1.addChild(this.arm_left_2);
        this.arm_right_1.addChild(this.arm_right_2);
        this.screw_1.addChild(this.screw_2);
        this.sholder_left.addChild(this.arm_left_1);
        this.head.addChild(this.screw_1);
        this.sholder_right.addChild(this.arm_right_1);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        doNormalAnimation(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        EntityGolemCharge golem = (EntityGolemCharge) entity;
        int chTime = golem.getChargeTime();
        if(chTime > 0)
        {
            if(chTime < golem.getChargeTimeLimit()) doChargeAnimation(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            else
            {
                rotateArmAnimation();
                resetChargeAnimation();
            }
        }


        this.leg_right.render(scale);
        this.torso.render(scale);
        this.leg_left.render(scale);
        this.head.render(scale);
        this.sholder_left.render(scale);
        this.sholder_right.render(scale);
    }

    public void doNormalAnimation(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        setRotateAngle(leg_right, MathHelper.cos(limbSwing * 0.662f) * 1.4f * limbSwingAmount, 0f, 0f);
        setRotateAngle(leg_left, -(MathHelper.cos(limbSwing * 0.662f) * 1.4f * limbSwingAmount), 0f, 0f);

        setRotateAngle(sholder_left, MathHelper.cos(limbSwing * 0.662f) * 1.4f * limbSwingAmount, 0f, 0f);
        setRotateAngle(sholder_right, -(MathHelper.cos(limbSwing * 0.662f) * 1.4f * limbSwingAmount), 0f, 0f);
        arm_left_1.rotateAngleY = 0f;
        arm_right_1.rotateAngleY = 0f;

        setRotateAngle(head, headPitch * 0.017453292f, netHeadYaw * 0.017453292f, 0f);

        screw_1.rotateAngleY = 0f;
    }

    public void doChargeAnimation(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        long ms = System.currentTimeMillis() % 1024;
        float anim = (float)(ms * 0.0009765625d);
        screw_1.rotateAngleY = anim * 4f * (float)Math.PI;
        anim = anim > 0.5f? 0.5f - (anim - 0.5f) : anim;
        anim *= 2f;
        anim = anim > 0.5f? 0.5f - (anim - 0.5f) : anim;
        anim *= 2f;
        anim = anim > 0.5f? 0.5f - (anim - 0.5f) : anim;
        anim *= 2f;
        torso.rotateAngleX = anim * ((float)Math.PI * 0.05f) - ((float)Math.PI * 0.025f);
        head.rotateAngleX = anim * ((float)Math.PI * -0.05f) - ((float)Math.PI * -0.025f);
    }

    public void rotateArmAnimation()
    {
        long ms = System.currentTimeMillis() % 1024;
        float anim = (float)(ms * 0.0009765625d);
        torso.rotateAngleX = 0;
        arm_left_1.rotateAngleY = anim * 8f * (float)Math.PI;
        arm_right_1.rotateAngleY = anim * -8f * (float)Math.PI;
        sholder_right.rotateAngleX = (float)Math.PI * -0.6f;
        sholder_left.rotateAngleX = anim * 8f * (float)Math.PI;;
    }


    public void resetChargeAnimation()
    {
        screw_1.rotateAngleY = 0f;
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }


}
