package com.gj.baba.entities.ai;

import com.gj.baba.entities.entity.EntityGolemCharge;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

public class AIGolemChargeAttack<T extends EntityLivingBase> extends EntityAINearestAttackableTarget
{
    EntityGolemCharge golem;

    public AIGolemChargeAttack(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
        super((EntityCreature) creature, classTarget, checkSight);
        golem = (EntityGolemCharge) creature;
    }

    public AIGolemChargeAttack(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby)
    {
        super(creature, classTarget, checkSight, onlyNearby);
        golem = (EntityGolemCharge) creature;
    }

    public AIGolemChargeAttack(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T > targetSelector)
    {
        super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
    }

    @Override
    public void updateTask() {
        super.updateTask();

        int currCharge = golem.getChargeTime() + 1;

        if(currCharge > 200)
        {
            currCharge = 1;
        }

        if(currCharge == 1)
        {
            golem.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0f);
            golem.setAIMoveSpeed(0f);
        }
        else if(currCharge == golem.getChargeTimeLimit() + 1)
        {
            golem.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5f);
            golem.setAIMoveSpeed(0.5f);
        }
        this.golem.setChargeTime(currCharge);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.golem.setChargeTime(0);
        golem.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        golem.setAIMoveSpeed(0.23000000417232513f);
    }
}
