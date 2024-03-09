package com.gj.baba.capabilities;

import com.gj.baba.libraries.tinymap.TinyMapBuilder;
import net.minecraft.advancements.critereon.VillagerTradeTrigger;
import net.minecraft.block.BlockAnvil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class StatsSystem
{
    @CapabilityInject(IBabaStats.class)
    public static final Capability<IBabaStats> CAPABILITY = null;

    public static final EnumFacing sideFinal = EnumFacing.EAST;

    public static class BabaServerStats implements IBabaStats
    {
        private TinyMapBuilder<EnumStats, ArrayList<StatOperation>> statModifiers;
        int tempCount = 0;
        private int[] temporaryFlags;
        public BabaServerStats()
        {
            ArrayList<EnumStats> allstats = EnumStats.values;
            statModifiers = new TinyMapBuilder<EnumStats, ArrayList<StatOperation>>(allstats.size());
            for(int i = 0; i < allstats.size(); ++i)
            {
                statModifiers.put(allstats.get(i), new ArrayList<StatOperation>(5));
            }

            temporaryFlags = new int[10];
        }

        @Override
        public float applyStatModifiers(float original, EnumStats modType)
        {
            float result = original;
            ArrayList<StatOperation> ops = statModifiers.get(modType);
            if(ops != null)
            {
                for(int i = 0; i < ops.size(); ++i)
                {
                    result = ops.get(i).operate(result);
                }
            }

            return result;
        }

        @Override
        public void addStatModifier(EnumStats modType, float opValue, OperationType opType)
        {
            statModifiers.get(modType).add(StatOperation.getOne(opValue, opType));
        }

        @Override
        public void resetStatModifiers()
        {
            ArrayList<EnumStats> allStats = EnumStats.values;
            for(int i = 0; i < allStats.size(); ++i)
            {
                ArrayList<StatOperation> ops = statModifiers.get(allStats.get(i));
                if(ops.size() > 0)
                {
                    for(int i2 = ops.size(); i2 > 0; --i2)
                    {
                        StatOperation.RecycleOne(ops.remove(i2 - 1));
                    }
                }
            }
        }

        public boolean hasTempFlag(int flag)
        {
            boolean has = false;
            for(int i = 0; i < tempCount; ++i)
            {
                if(temporaryFlags[i] == flag)
                {
                    has = true;
                    break;
                }
            }

            return has;
        }

        public void setTempFlag(int flag)
        {
            if(temporaryFlags.length == tempCount)
            {
                int[] substitute = new int[tempCount + 5];
                for(int i = 0; i < tempCount; ++i)
                {
                    substitute[i] = temporaryFlags[i];
                }
                temporaryFlags = substitute;
            }
            temporaryFlags[tempCount] = flag;
            ++tempCount;
        }

        public void resetTempFlags()
        {
            tempCount = 0;
        }

    }
    public interface IBabaStats
    {
        float applyStatModifiers(float original, EnumStats modType);
        void addStatModifier(EnumStats modType, float opValue, OperationType opType);
        void resetStatModifiers();

        boolean hasTempFlag(int flag);
        void setTempFlag(int flag);
        void resetTempFlags();
    }

    public static class Storage implements Capability.IStorage<IBabaStats>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IBabaStats> capability, IBabaStats instance, EnumFacing side) {
            if(side != sideFinal) return null;
            NBTTagCompound compound = new NBTTagCompound();



            return compound;
        }

        @Override
        public void readNBT(Capability<IBabaStats> capability, IBabaStats instance, EnumFacing side, NBTBase nbt) {
            if(side != sideFinal) return;
        }
    }

    public static class EnumStats
    {
        static ArrayList<EnumStats> values = null;
        public static final EnumStats SATURATION_DEPLETION = new EnumStats(0);

        int id;
        public EnumStats(int id)
        {
            this.id = id;
            ArrayList<EnumStats> vs;
            if(values == null) values = new ArrayList<EnumStats>();
            values.add(this);
        }

        @Override
        public int hashCode() {

            return id;
        }
    }

    public static final class StatOperation
    {
        OperationType operation;
        float value;

        static ArrayList<StatOperation> pool = new ArrayList<StatOperation>(200);;

        private StatOperation(float value, OperationType op)
        {
            this.operation = op;
            this.value = value;
        }

        public static StatOperation getOne(float value, OperationType op)
        {
            if(pool == null || pool.size() == 0)
                return new StatOperation(value, op);

            return pool.remove(pool.size() - 1).rebrand(value, op);
        }

        public static void RecycleOne(StatOperation so)
        {
            if(pool != null) pool.add(so);
        }

        private StatOperation rebrand(float value, OperationType op)
        {
            this.value = value;
            this.operation = operation;
            return this;
        }

        public float operate(float subject)
        {
            return operation.operate(subject, value);
        }
    }

    public static class OperationType
    {
        public static final OperationType ADD = new Add(0);
        public static final OperationType SUB = new Sub(1);
        public static final OperationType Mul = new Mul(2);
        public static final OperationType Div = new Div(3);

        private final int id;
        private OperationType(int id){this.id = id;}

        public float operate(float subject, float modifier)
        {
            return 0f;
        }

        @Override
        public int hashCode() {
            return id;
        }

        private static final class Add extends OperationType
        {
            private Add(int id) {
                super(id);
            }

            public float operate(float subject, float modifier) {return subject + modifier;}
        }
        private static final class Sub extends OperationType
        {
            private Sub(int id) {
                super(id);
            }

            public float operate(float subject, float modifier) {return subject - modifier;}
        }
        private static final class Mul extends OperationType
        {
            private Mul(int id) {
                super(id);
            }

            public float operate(float subject, float modifier) {return subject * modifier;}
        }
        private static final class Div extends OperationType
        {
            private Div(int id) {
                super(id);
            }

            public float operate(float subject, float modifier) {return subject / modifier;}
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase>
    {
        private final IBabaStats stats;

        public Provider()
        {
            stats = new BabaServerStats();
        }

        @Override
        public NBTBase serializeNBT() {
            return CAPABILITY.getStorage().writeNBT(CAPABILITY, stats, EnumFacing.EAST);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            CAPABILITY.getStorage().readNBT(CAPABILITY, stats, EnumFacing.EAST, nbt);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY & facing == sideFinal ? CAPABILITY.cast(stats) : null;
        }
    }

    public static class Factory implements Callable<IBabaStats>
    {
        @Override
        public IBabaStats call() throws Exception {
            return new BabaServerStats();
        }
    }
}
