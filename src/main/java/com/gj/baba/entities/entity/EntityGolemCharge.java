package com.gj.baba.entities.entity;

import com.gj.baba.entities.ai.AIGolemChargeAttack;
import net.minecraft.block.BlockNote;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;

import java.util.List;

public class EntityGolemCharge extends EntityMob
{
    static final DataParameter<Integer> CHARGE_TIME = EntityDataManager.createKey(EntityGolemCharge.class, DataSerializers.VARINT);
    static final DataParameter<Integer> CHARGE_TIME_LIMIT = EntityDataManager.createKey(EntityGolemCharge.class, DataSerializers.VARINT);

    private int cooldown = 0;
    private boolean canAttack = false;
    public EntityGolemCharge(World worldIn) {
        super(worldIn);
        this.setSize(0.8F, 1.5f);
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new AIGolemChargeAttack(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(CHARGE_TIME, 0);
        dataManager.register(CHARGE_TIME_LIMIT, 100);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(world.isRemote || !this.isEntityAlive()) return;

        int charge = getChargeTime();
        int limit = getChargeTimeLimit();

        if(charge > limit)
        {
            if(cooldown == 0)
            {
                EntityLivingBase target = this.getAttackTarget();

                if(this.getAttackReachSqr(target) >= this.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ))
                {
                    canAttack = true;
                    this.attackEntityAsMob(target);
                    canAttack = false;
                    cooldown = 10;
                }
            }
            else --cooldown;
        }

        if(charge != 0 && charge < limit)
        {
            //SoundEvents.BLOCK_NOTE_HAT
            if(charge % 5 == 0) this.playSound(SoundEvents.BLOCK_NOTE_HAT, 1f, (charge / (float)limit) * 1.2f);
        }
        if(charge == limit) this.playSound(SoundEvents.BLOCK_NOTE_HAT, 1f, (charge / (float)limit) * 1.4f);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean success = super.attackEntityFrom(source, amount);
        int cTime = getChargeTime();
        if(success && cTime > 0 && cTime < getChargeTimeLimit())
        {
            setChargeTime(1);
            this.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, 1f, 0.7f);
        }
        else
        {
            this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.4f, 0.5f);
        }
        return success;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        if(!canAttack) return false;
        return super.attackEntityAsMob(entityIn);
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return (double)(this.width * 2.0F * this.width * 2.0F + attackTarget.width);
    }

    public int getChargeTime()
    {
        return this.getDataManager().get(CHARGE_TIME);
    }

    public void setChargeTime(int value)
    {
        this.getDataManager().set(CHARGE_TIME, Integer.valueOf(value));
    }

    public int getChargeTimeLimit()
    {
        return this.getDataManager().get(CHARGE_TIME_LIMIT);
    }

    public void setChargeTimeLimit(int value)
    {
        this.getDataManager().set(CHARGE_TIME_LIMIT, Integer.valueOf(value));
    }
}
