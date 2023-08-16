package com.gj.baba.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BabaUtil
{
    public static void broadcastMessage(World world, String message)
    {
        TextComponentString comp = new TextComponentString(message);

        for(EntityPlayer player : world.playerEntities)
        {
            player.sendMessage(comp);
        }
    }

    public static int toChunkFormat(int x_z)
    {
        return x_z >> 4;
    }

    public static class IntSerializer
    {
        private static IntSerializer INSTANCE;

        public static IntSerializer getClearSingleton()
        {
            if(INSTANCE == null) INSTANCE = new IntSerializer();
            INSTANCE.reset();
            return INSTANCE;
        }

        int[] data = new int[1024];

        int count = 0;

        public void write(int value)
        {
            if(count == data.length)
            {
                int[] oldc = data;
                int[] newc = new int[oldc.length * 2];
                for(int i = 0; i < data.length; ++i)
                {
                    newc[i] = oldc[i];
                }
                data = newc;
            }

            data[count] = value;
            ++count;
        }

        public int[] toArray()
        {
            int[] toreturn = new int[count];
            for(int i = 0; i < count; ++i) toreturn[i] = data[i];

            return toreturn;
        }

        private void reset()
        {
            count = 0;
        }
    }

    public static class IntDeserializer
    {
        private static IntDeserializer INSTANCE;

        public static IntDeserializer getClearSingleton(int[] subject)
        {
            if(INSTANCE == null) INSTANCE = new IntDeserializer();
            INSTANCE.reset(subject);
            return INSTANCE;
        }

        int[] data;
        int readIndx = 0;

        public int read()
        {
            int value = data[readIndx];
            ++readIndx;
            return value;
        }

        public int remaining()
        {
            return data.length - readIndx;
        }

        private void reset(int[] subject)
        {
            data = subject;
            readIndx = 0;
        }

        public void dispose()
        {
            data = null;
        }
    }
}
