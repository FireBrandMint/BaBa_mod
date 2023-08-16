package com.gj.baba.capabilities;


import com.gj.baba.BaBa;
import com.gj.baba.libraries.tinymap.TinyMapBuilder;
import com.gj.baba.util.BabaUtil;
import com.gj.baba.util.GChunkBlockCoords;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
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

    static int passCount = 0, passPoint = 60;

    public static boolean tryAddGas(World world, BlockPos pos, Gas gas)
    {
        IGasMatrix matrix = world.getChunkFromBlockCoords(pos).getCapability(GAS_CAPABILITY, capaSide);
        boolean success = world.isBlockLoaded(pos) && world.isAirBlock(pos);
        if(success) matrix.set(world, pos, gas);
        return success;
    }

    public static void onAttachChunkCapabilities(AttachCapabilitiesEvent<Chunk> event)
    {
        GasSystem.GasProvider provider = new GasSystem.GasProvider(event.getObject());

        event.addCapability(new ResourceLocation(BaBa.ModId, "ggas"), provider);
    }

    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        ++passCount;
        if(passCount == passPoint)
            passCount = 0;
        else
            return;

        chunksToTick.forEach(TickInducer.INSTANCE);
    }

    public static void onChunkLoad(ChunkEvent.Load event)
    {
        Chunk chunk = event.getChunk();
        chunksToTick.put(chunk.getPos(), chunk);
    }

    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        Chunk chunk = event.getChunk();
        chunksToTick.remove(chunk.getPos());
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
        public void addContentsTo(Gas gas)
        {

        }

        public void serialize(BabaUtil.IntSerializer buf)
        {

        }

        public static Gas deserialize(BabaUtil.IntDeserializer buf)
        {
            return new Gas();
        }
    }

    public interface IGasMatrix
    {

        void tick(World worldIn, Chunk chunkIn);
        Gas get(BlockPos pos);

        boolean set(World world, BlockPos pos, Gas gas);

        boolean hasGas(BlockPos pos);

        NBTTagIntArray serialize();

        void deserialize(NBTTagIntArray subject);
    }


    public static class GasStorage implements Capability.IStorage<IGasMatrix>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IGasMatrix> capability, IGasMatrix instance, EnumFacing side)
        {
            NBTTagCompound compound = new NBTTagCompound();
            if(side != capaSide) return compound;
            compound.setTag("gasStorage", instance.serialize());

            return compound;
        }

        @Override
        public void readNBT(Capability<IGasMatrix> capability, IGasMatrix instance, EnumFacing side, NBTBase nbt)
        {
            if (side == capaSide && nbt instanceof NBTTagCompound)
            {
                instance.deserialize((NBTTagIntArray) ((NBTTagCompound) nbt).getTag("gasStorage"));
            }
        }


    }

    public static class DefaultPollution implements IGasMatrix
    {
        public static ArrayList<GasEntry> cache = new ArrayList<GasEntry>(100);

        public static BiConsumer<GChunkBlockCoords, Gas> fillCache = new BiConsumer<GChunkBlockCoords, Gas>() {
            @Override
            public void accept(GChunkBlockCoords key, Gas value) {
                cache.add(new GasEntry(key, value));
            }
        };

        static Random rand = new Random(System.nanoTime());

        private TinyMapBuilder<GChunkBlockCoords, Gas> gasMap = new TinyMapBuilder<GChunkBlockCoords, Gas>();

        @Override
        public void tick(World world, Chunk chunkIn)
        {
            int amount = gasMap.size() < 25 ? gasMap.size() : 25;

            gasMap.forEach(fillCache);

            for(int i = 0; i < amount; ++i)
            {
                if(gasMap.size() == 0) break;

                int toProcess = rand.nextInt(cache.size());

                GasEntry currEntry = cache.get(toProcess);

                GChunkBlockCoords key = currEntry.key;
                Gas value = currEntry.value;

                int direction = rand.nextInt(3);
                switch (direction)
                {
                    case 0:
                        tryMove(world, chunkIn, toProcess, key, value, rand.nextInt(3) - 1, 0, 0);
                        break;
                    case 1:
                        tryMove(world, chunkIn, toProcess, key, value,0, rand.nextInt(3) - 1 , 0);
                        break;
                    case 2:
                        tryMove(world, chunkIn, toProcess, key, value,0, 0 , rand.nextInt(3) - 1);
                        break;
                }
            }

            cache.clear();
        }

        private void tryMove(World world, Chunk chunkIn, int ind, GChunkBlockCoords key, Gas value, int x, int y, int z)
        {
            boolean success = false;

            int chunkX = chunkIn.x;
            int chunkZ = chunkIn.z;

            BlockPos currPos = GChunkBlockCoords.toNormalCoords(key, chunkX, chunkZ);
            BlockPos nextPos = currPos.add(x,y,z);

            int nextChunkX = BabaUtil.toChunkFormat(x);
            int nextChunkZ = BabaUtil.toChunkFormat(z);

            if(chunkX == nextChunkX & chunkZ == nextChunkZ)
            {
                if(!hasGas(nextPos))
                {
                    gasMap.remove(key);
                    cache.remove(ind);
                    set(world, nextPos, value);

                    success = true;
                }
            }
            else if(world.isBlockLoaded(nextPos))
            {
                Chunk nextChunk = world.getChunkFromBlockCoords(nextPos);
                IGasMatrix nextMatrix = nextChunk.getCapability(GAS_CAPABILITY, capaSide);

                if(!nextMatrix.hasGas(nextPos))
                {
                    cache.remove(ind);
                    gasMap.remove(key);
                    chunkIn.markDirty();
                    nextMatrix.set(world, nextPos, value);

                    success = true;
                }
            }

            if(success)
            {

                BabaUtil.broadcastMessage(world,"Air moved to: " + nextPos.toString());
            }
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

            if(gasMap.containsKey(pos))
            {
                gas.addContentsTo(gasMap.get(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4)));
            }
            else
            {
                gasMap.put(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4), gas);
            }

            return true;
        }

        public boolean hasGas(BlockPos pos)
        {
            return gasMap.containsKey(GChunkBlockCoords.fromNormalCoords(pos, pos.getX() >> 4, pos.getZ() >> 4));
        }

        public NBTTagIntArray serialize()
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

        public void deserialize(NBTTagIntArray subject)
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
    public static class SafePollution extends DefaultPollution
    {
        private final Chunk chunk;

        public SafePollution(Chunk chunk)
        {
            this.chunk = chunk;
        }

        @Override
        public boolean set(World world, BlockPos pos, Gas gas) {
            if(super.set(world, pos, gas))
            {
                chunk.markDirty();
                return true;
            }

            return false;
        }
    }

    public static class GasProvider implements ICapabilitySerializable<NBTBase>
    {
        private final IGasMatrix matrix;

        public GasProvider(Chunk chunk)
        {
            matrix = new SafePollution(chunk);
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
            return new DefaultPollution();
        }
    }
}
