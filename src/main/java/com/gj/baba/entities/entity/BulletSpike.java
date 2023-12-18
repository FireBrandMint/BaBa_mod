package com.gj.baba.entities.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BulletSpike extends Entity implements IThrowableEntity
{
    private static final DataParameter<Float> sizeOrb = EntityDataManager.createKey(BulletSpike.class, DataSerializers.FLOAT);

    float damage = 0;

    Entity thrower;

    public BulletSpike(World worldIn) {
        super(worldIn);

        dataManager.register(sizeOrb, 2f);

        this.setSize(1f, 1f);

        motionZ = 0.1f;
    }

    public BulletSpike(World worldIn, float damage, float x, float y, float z) {
        super(worldIn);

        dataManager.register(sizeOrb, 2f);

        this.setSize(1f, 1f);

        this.setPosition(x,y,z);

        this.damage = damage;
    }

    public BulletSpike(World worldIn, Entity thrower, float damage, float x, float y, float z) {
        super(worldIn);

        this.thrower = thrower;

        dataManager.register(sizeOrb, 2f);

        this.setSize(1f, 1f);

        this.setPosition(x,y,z);

        this.damage = damage;
    }

    protected void entityInit()
    {

    }

    protected void setOrbSize(float i)
    {
        dataManager.set(sizeOrb, i);
    }

    public float getOrbSize()
    {
        return dataManager.get(sizeOrb);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        NBTTagCompound thisProperties;

        if(compound.hasKey("bulletProperties"))
            thisProperties = compound.getCompoundTag("bulletProperties");
        else
        {
            thisProperties = new NBTTagCompound();
            compound.setTag("bulletProperties", thisProperties);
        }

        thisProperties.setFloat("size", this.getOrbSize());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        if(!compound.hasKey("bulletProperties")) return;

        NBTTagCompound bp = compound.getCompoundTag("bulletProperties");

        if(bp.hasKey("size"))
        {
            this.setOrbSize(bp.getFloat("size"));
        }
    }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    public boolean canBeCollidedWith()
    {
        return false;
    }

    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public void onUpdate() {
        //super.onUpdate();

        boolean dead = false;

        Vec3d nowPos = new Vec3d(posX, posY, posZ);
        Vec3d nextPos = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

        if(!this.world.isRemote)
        {
            if(!world.isAirBlock(new BlockPos(nowPos)) || !world.isAirBlock(new BlockPos(nextPos))) dead = true;

            RayTraceResult rtr = this.world.rayTraceBlocks(nowPos, nextPos, false);

            if(rtr != null)
            {
                if(rtr.typeOfHit != RayTraceResult.Type.MISS) dead = true;
            }

            List<Entity> collided = getEntitesColliding();

            for(int i = 0; i < collided.size(); ++i)
            {
                Entity curr = collided.get(i);

                if(curr.canBeAttackedWithItem() && curr.canBeCollidedWith() && curr != thrower)
                {
                    if(curr.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), damage))
                        dead = true;
                }
            }
        }
        else
        {
            this.setInvisible(world.isAirBlock(new BlockPos(nowPos)));
        }

        this.setPosition(nextPos.x, nextPos.y, nextPos.z);

        if(dead) setDead();
    }

    private List<Entity> getEntitesColliding ()
    {
        return this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().shrink(0.1d));
    }

    @Override
    public void onEntityUpdate() {

    }

    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @Override
    public Entity getThrower() {
        return this.thrower;
    }

    @Override
    public void setThrower(Entity entity) {
        this.thrower = entity;
    }

    /*
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        d0 *= this.getOrbSize();
        return distance < d0 * d0;
    }

     */
}
