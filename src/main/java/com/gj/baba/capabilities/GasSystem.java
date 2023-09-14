package com.gj.baba.capabilities;


import com.gj.baba.BaBa;
import com.gj.baba.components.substances.Substance;
import com.gj.baba.libraries.tinymap.TinyMapBuilder;
import com.gj.baba.util.BabaUtil;
import com.gj.baba.util.GChunkBlockCoords;
import com.gj.baba.util.GObjectPools;
import net.minecraft.block.BlockLiquid;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class GasSystem
{
    @CapabilityInject(IGasMatrix.class)
    public static final Capability<IGasMatrix> GAS_CAPABILITY = null;

    public static final EnumFacing capaSide = EnumFacing.UP;

    private static TinyMapBuilder<ChunkPos, Chunk> chunksToTick = new TinyMapBuilder<ChunkPos, Chunk>(2000);

    private static ArrayList<TinyMapBuilder<GChunkBlockCoords, Gas>> matrixMapsToCache = new ArrayList<TinyMapBuilder<GChunkBlockCoords, Gas>>(50);

    public static int maxCubeTicksPerChunk = 10;
    static int posponeTickCount = 0, stopPosponePoint = 5;

    public static boolean tryInjectGas(World world, BlockPos pos, Gas gas)
    {
        IGasMatrix matrix = getMatrix(world, pos);
        boolean success = world.isBlockLoaded(pos) && world.isAirBlock(pos);
        if(success)
        {
            matrix.set(world, pos, gas);
        }
        return success;
    }

    @Nullable
    public static Gas tryGetGas(World world, BlockPos pos)
    {
        IGasMatrix matrix = getMatrix(world, pos);
        boolean success = world.isBlockLoaded(pos) && world.isAirBlock(pos);
        if(success)
        {
            return matrix.get(pos);
        }
        return null;
    }

    @Nullable
    public static Gas removeGas(World world, BlockPos pos)
    {
        IGasMatrix matrix = getMatrix(world, pos);
        boolean success = world.isBlockLoaded(pos) && world.isAirBlock(pos);
        if(success)
        {
            Gas curr = matrix.get(pos);
            if(curr != null) matrix.set(world, pos, null);
            return curr;
        }

        return null;
    }
    @Nullable
    public static Substance removeSubstanceFromGas(World world, BlockPos pos, int substanceId)
    {
        IGasMatrix matrix = getMatrix(world, pos);
        boolean success = world.isBlockLoaded(pos) && world.isAirBlock(pos);
        if(success)
        {
            return matrix.get(pos).removeSubstanceAndRetrieve(substanceId);
        }

        return null;
    }

    public static IGasMatrix getMatrix(World world, BlockPos pos)
    {
        return world.getChunkFromBlockCoords(pos).getCapability(GAS_CAPABILITY, capaSide);
    }

    public static void OnAttachChunkCapabilities(AttachCapabilitiesEvent<Chunk> event)
    {
        GasSystem.GasProvider provider = new GasSystem.GasProvider(event.getObject());

        event.addCapability(new ResourceLocation(BaBa.ModId, "ggas"), provider);
    }

    public static void OnServerTick(TickEvent.ServerTickEvent event)
    {
        for(int i = 0; i < matrixMapsToCache.size(); ++i)
        {
            TinyMapBuilder<GChunkBlockCoords, Gas> curr = matrixMapsToCache.get(i);
            curr.clear();
            GObjectPools.storeGasMatrixMap(curr);
        }

        matrixMapsToCache.clear();

        ++posponeTickCount;
        if(posponeTickCount == stopPosponePoint)
            posponeTickCount = 0;
        else
            return;

        chunksToTick.forEach(TickInducer.INSTANCE);
    }

    public static void OnChunkLoad(ChunkEvent.Load event)
    {
        if(event.getChunk().getWorld().isRemote) return;
        Chunk chunk = event.getChunk();
        chunksToTick.put(chunk.getPos(), chunk);
    }

    public static void OnChunkUnload(ChunkEvent.Unload event)
    {
        if(event.getChunk().getWorld().isRemote) return;
        Chunk chunk = event.getChunk();
        chunksToTick.remove(chunk.getPos());
        matrixMapsToCache.add( ((DefaultGasMatrix)event.getChunk().getCapability(GAS_CAPABILITY, capaSide)).gasMap);
    }

    private static class TickInducer implements BiConsumer<ChunkPos, Chunk> {
        public static TickInducer INSTANCE = new TickInducer();

        //processes each chunk
        @Override
        public void accept(ChunkPos chunkPos, Chunk chunk)
        {
            IGasMatrix matrix = chunk.getCapability(GAS_CAPABILITY, capaSide);
            matrix.tick(chunk.getWorld(), chunk);
        }
    }

    public static class Gas
    {
        int count = 0;
        Substance[] subs = new Substance[3];

        boolean updated = true;

        double gramPerMole = 0.0;
        double kpa = 0.0;
        double moles = 0.0;
        double temperatureK = 0.0;

        private void reset()
        {
            count = 0;
            for(int i = 0; count > i; ++i)
            {
                subs[i] = null;
            }
            temperatureK = 0.0;
            updated = true;
        }

        public Gas clone()
        {
            Gas clone = new Gas();
            clone.count = this.count;
            for(int i = 0; i < count; ++i)
            {
                clone.subs[i] = subs[i].cloneSelf();
            }
            clone.updated = this.updated;
            clone.gramPerMole = this.gramPerMole;
            clone.kpa = this.kpa;
            clone.moles = this.moles;
            clone.temperatureK = this.temperatureK;

            return clone;
        }

        public void mixWithSelf(Substance sub)
        {
            double heat = Substance.finalMixtureHeat(
                    (float)this.getMoles(), (float)this.temperatureK, this.getGramPerMole(),
                    sub.getMoles(), sub.getTemperatureK(), sub.getGramsPerMole()
            );

            this.setTemperature(heat);

            this.addSubstance(sub);
        }

        public void mixWithSelf(Gas gas)
        {
            float molesThis = (float)this.getMoles();
            double finalHeat = Substance.finalMixtureHeat(molesThis, (float)this.temperatureK, this.getGramPerMole(),
                    (float)gas.getMoles(), (float)gas.temperatureK, gas.getGramPerMole());

            for(int i = 0; i < count; ++i)
            {
                Substance curr = this.subs[i];
                this.addSubstance(curr);
            }

            this.setTemperature(finalHeat);

            gas.reset();
        }

        public void transferNaturallyTo(Gas gas)
        {
            float molesThis = (float)this.getMoles();

            double percentage = Substance.getTransferPercentage(
                    molesThis, (float)(this.getKPA()),
                    (float)(gas.getKPA()));

            if(percentage == 0.0) return;

            double finalHeat = Substance.finalMixtureHeat((float)(molesThis * percentage), (float)this.temperatureK, this.getGramPerMole(),
                    (float)gas.getMoles(), (float)gas.temperatureK, gas.getGramPerMole());

            for(int i = 0; i < count; ++i)
            {
                Substance curr = this.subs[i];
                Substance clone = curr.cloneSelf();
                curr.setMoles((float)(curr.getMoles() * (1.0 - percentage)));
                clone.setMoles((float)(clone.getMoles() * percentage));
                gas.addSubstance(clone);
            }
            this.updated = true;
            gas.setTemperature(finalHeat);
        }
        public void transferNaturallyTo(Gas gas, double percent)
        {
            if(percent > 1.0 | percent < 0.0) throw new RuntimeException("Percentage can't be above 1 or below 0");

            float molesThis = (float)this.getMoles();

            double percentage = Substance.getTransferPercentage(
                    molesThis, (float)(this.getKPA()),
                    (float)(gas.getKPA()));

            if(percentage == 0.0) return;
            percentage *= percent;

            double finalHeat = Substance.finalMixtureHeat((float)(molesThis * percentage), (float)this.temperatureK, this.getGramPerMole(),
                    (float)gas.getMoles(), (float)gas.temperatureK, gas.getGramPerMole());

            for(int i = 0; i < count; ++i)
            {
                Substance curr = this.subs[i];
                Substance clone = curr.cloneSelf();
                curr.setMoles((float)(curr.getMoles() * (1.0 - percentage)));
                clone.setMoles((float)(clone.getMoles() * percentage));
                gas.addSubstance(clone);
            }
            this.updated = true;
            gas.setTemperature(finalHeat);
        }

        private void addSubstance(Substance gas)
        {
            boolean failure = true;

            for(int i = 0; i < count; ++i)
            {
                Substance curr = subs[i];
                if(curr.getID() == gas.getID())
                {
                    curr.addMolesToThis(gas);

                    failure = false;
                }
            }

            if(failure)
            {
                this.addData(gas);
            }

            updated = true;
        }

        public int getCount()
        {
            return count;
        }

        public double getGramPerMole()
        {
            updateVars();

            return gramPerMole;
        }

        private Gas slice(double cut)
        {
            Gas cloneGas = new Gas();
            cloneGas.setTemperature(this.temperatureK);
            for(int i = 0; i < count; ++i)
            {
                Substance curr = subs[i];

                cloneGas.addSubstance(curr.slicePorcentage(cut));
            }

            return cloneGas;
        }

        public double getTemperatureK()
        {
            return temperatureK;
        }

        public double getKPA()
        {
            updateVars();

            return kpa;
        }

        public double getMoles()
        {
            updateVars();
            return moles;
        }

        public void setTemperature(double kelvin)
        {
            float k = (float) kelvin;
            for(int i = 0; i < count; ++i)
            {
                subs[i].setTemperatureK(k);
            }

            temperatureK = kelvin;
        }

        private void updateVars()
        {
            if(!updated) return;
            double _kpa = 0.0;
            double _moles = 0.0;
            double gm = 0.0;

            if(count == 0)
            {
                moles = 0.0;
                kpa = 0.0;
                gramPerMole = 0.0;
                updated = false;
                return;
            }

            for(int i = 0; i < count; ++i)
            {
                Substance curr = subs[i];
                float currMoles = curr.getMoles();
                if(currMoles <= 0f)
                {
                    removeData(i);
                    --i;
                }
                _moles += currMoles;
                gm += currMoles * curr.getGramsPerMole();
                _kpa += curr.getKPA(1.0f);
            }

            moles = _moles;
            kpa = _kpa;

            if(_moles > 0.0) gm /= _moles;
            gramPerMole = gm;

            updated = false;
        }

        public void serialize(BabaUtil.IntSerializer buf)
        {
            buf.write((int)(temperatureK * 100.0));
            buf.write(count);
            for(int i = 0; i < count; ++i)
            {
                subs[i].Serialize(buf);
            }
        }

        public static Gas deserialize(BabaUtil.IntDeserializer buf)
        {
            Gas gas = new Gas();

            double temperature = buf.read() * 0.01;
            int _count = buf.read();
            for(int i = 0; i < _count; ++i)
            {
                 gas.addSubstance(Substance.Deserialize(buf));
            }

            gas.temperatureK = temperature;

            return gas;
        }

        private Substance getSubstanceByIndex(int i)
        {
            return subs[i];
        }

        @Nullable
        public Substance getSubstanceById(int substanceId)
        {
            for(int i = 0; i < count; ++i)
            {
                Substance curr = subs[i];
                if(curr.getID() == substanceId)
                {
                    updated = true;
                    return curr;
                }
            }
            return null;
        }

        private void addData(Substance sub)
        {
            int sl = subs.length;
            if(sl == count)
            {
                Substance[] newarr = new Substance[sl + 3];

                for(int i = 0; i < sl; ++i)
                {
                    newarr[i] = subs[i];
                }
                subs = newarr;
            }

            subs[count] = sub;

            updated = true;

            ++count;
        }
        private void removeData(int index)
        {
            if(count == 0) return;
            int lastElement = count - 1;
            for(int i = index; i < lastElement; ++i)
            {
                subs[i] = subs[i + 1];
            }

            subs[lastElement] = null;
            --count;
            updated = true;
        }

        /**
         * Returns true if substance existed.
         * @param substanceId
         * @return
         */
        public boolean removeSubstance(int substanceId)
        {
            boolean isFound = false;
            int index = -1;
            for(int i = 0; i < count; ++i)
            {
                if(subs[i].getID() == substanceId)
                {
                    isFound = true;
                    index = i;
                    break;
                }
            }

            if(isFound)
                removeData(index);

            return isFound;
        }

        public Substance removeSubstanceAndRetrieve(int substanceId)
        {
            boolean isFound = false;
            int index = -1;
            Substance s = null;
            for(int i = 0; i < count; ++i)
            {
                if(subs[i].getID() == substanceId)
                {
                    isFound = true;
                    index = i;
                    s = subs[i];
                    break;
                }
            }

            if(isFound)
                removeData(index);

            return s;
        }
    }

    public interface IGasMatrix
    {

        void tick(World worldIn, Chunk chunkIn);
        Gas get(BlockPos pos);

        boolean set(World world, BlockPos pos, Gas gas);

        void inject(World world, BlockPos pos, Gas gas);

        boolean hasGas(BlockPos pos);

        NBTTagIntArray Serialize();

        void Deserialize(NBTTagIntArray subject);
    }


    public static class GasStorage implements Capability.IStorage<IGasMatrix>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IGasMatrix> capability, IGasMatrix instance, EnumFacing side)
        {
            NBTTagCompound compound = new NBTTagCompound();
            if(side != capaSide) return compound;
            compound.setTag("gasStorage", instance.Serialize());

            return compound;
        }

        @Override
        public void readNBT(Capability<IGasMatrix> capability, IGasMatrix instance, EnumFacing side, NBTBase nbt)
        {
            if (side == capaSide && nbt instanceof NBTTagCompound)
            {
                instance.Deserialize((NBTTagIntArray) ((NBTTagCompound) nbt).getTag("gasStorage"));
            }
        }


    }

    public static class DefaultGasMatrix implements IGasMatrix
    {
        public static ArrayList<GChunkBlockCoords> cache = new ArrayList<GChunkBlockCoords>(100);

        public static BiConsumer<GChunkBlockCoords, Gas> fillCache = new BiConsumer<GChunkBlockCoords, Gas>() {
            @Override
            public void accept(GChunkBlockCoords key, Gas value) {
                cache.add(key);
            }
        };

        static Random rand = new Random(System.nanoTime());

        private TinyMapBuilder<GChunkBlockCoords, Gas> gasMap = GObjectPools.getGasMatrixMap(50);

        @Override
        public void tick(World world, Chunk chunkIn)
        {
            int amount = gasMap.size() < maxCubeTicksPerChunk ? gasMap.size() : maxCubeTicksPerChunk;

            gasMap.forEach(fillCache);

            for(int i = 0; i < amount; ++i)
            {
                if(gasMap.size() == 0) break;

                int toProcess = rand.nextInt(cache.size());

                GChunkBlockCoords key = cache.get(toProcess);
                Gas value = gasMap.get(key);
                if(value == null)
                {
                    cache.remove(toProcess);
                    continue;
                }

                tryMove(world, chunkIn, toProcess, key, value);

                cache.remove(toProcess);
            }

            cache.clear();
        }
        private void tryMove(World world, Chunk chunkIn, int ind, GChunkBlockCoords key, Gas value)
        {
            boolean success = false;

            int chunkX = chunkIn.x;
            int chunkZ = chunkIn.z;

            BlockPos currPos = GChunkBlockCoords.toNormalCoords(key, chunkX, chunkZ);
            if(this.isVacuum(world, currPos))
            {
                gasMap.remove(key);
                return;
            }

            for(int i = 0; i < 6; ++i)
            {
                boolean moved = false;

                int x = 0, y = 0, z = 0;
                if(value.getKPA() < 1.0)
                {
                    moved = true;
                    int next = rand.nextInt(6);

                    switch (next) {
                        case 0:
                            y = 1;
                            break;
                        case 1:
                            y = -1;
                            break;
                        case 2:
                            x = 1;
                            break;
                        case 3:
                            x = -1;
                            break;
                        case 4:
                            z = 1;
                            break;
                        case 5:
                            z = -1;
                            break;
                    }
                }
                else {
                    switch (i) {
                        case 0:
                            y = 1;
                            break;
                        case 1:
                            y = -1;
                            break;
                        case 2:
                            x = 1;
                            break;
                        case 3:
                            x = -1;
                            break;
                        case 4:
                            z = 1;
                            break;
                        case 5:
                            z = -1;
                            break;
                    }
                }

                BlockPos nextPos = currPos.add(x,y,z);
                if(world.isBlockFullCube(nextPos) || world.getBlockState(nextPos).getBlock() instanceof BlockLiquid)
                {
                    if(moved) return;
                    continue;
                }

                int nextChunkX = BabaUtil.toChunkFormat(x);
                int nextChunkZ = BabaUtil.toChunkFormat(z);

                if(chunkX == nextChunkX & chunkZ == nextChunkZ)
                {
                    moved = moved | successTickMove(world, chunkIn, chunkIn, this, nextPos, ind, key, value);
                }
                else if(world.isBlockLoaded(nextPos))
                {
                    Chunk nextChunk = world.getChunkFromBlockCoords(nextPos);
                    IGasMatrix nextMatrix = nextChunk.getCapability(GAS_CAPABILITY, capaSide);

                    moved = moved | successTickMove(world, chunkIn, nextChunk, nextMatrix, nextPos, ind, key, value);
                }

                if(moved) break;
            }

            success = true;
            if(success)
            {
                String sub = "";
                int cv = value.count;

                for(int i = 0; i < cv; ++i)
                {
                    Substance an = value.getSubstanceByIndex(i);
                    sub += '[' + an.getName() + ',' + an.getMoles() + ',' + an.getTemperatureK() + "K],";
                }

                sub += value.getKPA() + "KPA";
                BabaUtil.broadcastMessage(world,sub + " is in: " + currPos.toString());
            }
        }

        private boolean successTickMove(World world, Chunk chunkIn, Chunk nextChunk, IGasMatrix nextMatrix, BlockPos nextPos, int ind, GChunkBlockCoords key, Gas thisValue)
        {
            boolean moved = false;

            boolean has = nextMatrix.hasGas(nextPos);

            Gas nextGas = has? nextMatrix.get(nextPos) : null;

            if(has && nextGas.getKPA() > 0.001)
            {
                thisValue.transferNaturallyTo(nextGas, 0.25);

                nextChunk.markDirty();

                chunkIn.markDirty();

                moved = false;
            }
            else
            {
                double kpa = thisValue.getKPA();
                if(kpa > 1.0)
                {
                    nextMatrix.set(world, nextPos, thisValue.slice(0.2));
                    moved = false;
                }
                else
                {
                    gasMap.remove(key);
                    nextMatrix.set(world, nextPos, thisValue);
                    moved = true;
                }
                chunkIn.markDirty();
            }

            return moved;
        }

        private boolean isVacuum(World world, BlockPos pos)
        {
            return pos.getY() > world.getPrecipitationHeight(pos).getY() || world.isBlockFullCube(pos);
        }

        @Override
        public Gas get(BlockPos pos) {
            return gasMap.get(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4));
        }

        @Override
        public boolean set(World world, BlockPos pos, @Nullable Gas gas)
        {
            if(gas == null)
            {
                boolean b1 = gasMap.containsKey(pos);
                gasMap.remove(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4));
                return b1;
            }
            gasMap.put(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4), gas);

            return true;
        }

        public void inject(World world, BlockPos pos, Gas gas)
        {
            if(gasMap.containsKey(pos))
            {
                Gas g = get(pos);

                g.mixWithSelf(gas);

                //gas.addContentsTo(gasMap.get(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4)));
            }
            else
            {
                set(world, pos, gas);
            }
        }

        public boolean hasGas(BlockPos pos)
        {
            return gasMap.containsKey(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4));
        }

        public NBTTagIntArray Serialize()
        {
            BabaUtil.IntSerializer buf = BabaUtil.IntSerializer.getClearSingleton();

            for(Object obj : gasMap.entrySet())
            {
                Map.Entry<GChunkBlockCoords, Gas> curr = (Map.Entry<GChunkBlockCoords, Gas>) obj;

                GChunkBlockCoords pos = curr.getKey();
                buf.write((int)pos.x);
                buf.write((int)pos.y);
                buf.write((int)pos.z);
                curr.getValue().serialize(buf);
            }

            return new NBTTagIntArray(buf.toArray());
        }

        public void Deserialize(NBTTagIntArray subject)
        {
            gasMap.clear();

            BabaUtil.IntDeserializer buf = BabaUtil.IntDeserializer.getClearSingleton(subject.getIntArray());

            while(buf.remaining() != 0)
            {
                int x = buf.read();
                int y = buf.read();
                int z = buf.read();
                Gas gas = Gas.deserialize(buf);

                gasMap.put(new GChunkBlockCoords((byte)x,(short)y,(byte)z), gas);
            }

            buf.dispose();
        }

        private static class GasEntry
        {
            public GChunkBlockCoords key;
            public Gas value;
            public GasEntry(GChunkBlockCoords _key, Gas _value)
            {
                key = _key;
                value = _value;
            }
        }
    }

    /**
     * Marks the chunk as dirty when the value changes.
     * Cannot be the default implementation because it requires a chunk in the constructor
     */
    public static class SafeGasMatrix extends DefaultGasMatrix
    {
        private final Chunk chunk;

        public SafeGasMatrix(Chunk chunk)
        {
            this.chunk = chunk;
        }

        @Override
        public boolean set (World world, BlockPos pos, Gas gas) {
            if(super.set(world, pos, gas))
            {
                chunk.markDirty();
                return true;
            }

            return false;
        }

        @Override
        public void inject (World world, BlockPos pos, Gas gas)
        {
            chunk.markDirty();
            super.inject(world, pos, gas);
        }
    }

    public static class GasProvider implements ICapabilitySerializable<NBTBase>
    {
        private final IGasMatrix matrix;

        public GasProvider(Chunk chunk)
        {
            matrix = new SafeGasMatrix(chunk);
        }

        @Override
        public NBTBase serializeNBT() {
            return GAS_CAPABILITY.getStorage().writeNBT(GAS_CAPABILITY, matrix, EnumFacing.UP);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            GAS_CAPABILITY.getStorage().readNBT(GAS_CAPABILITY, matrix, EnumFacing.UP, nbt);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == GAS_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == GAS_CAPABILITY & facing == capaSide ? GAS_CAPABILITY.cast(matrix) : null;
        }
    }

    public static class PolutionFactory implements Callable<IGasMatrix>
    {
        public IGasMatrix call()
        {
            return new DefaultGasMatrix();
        }
    }
}
