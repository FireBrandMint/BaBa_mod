package com.gj.baba.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BabaUtil
{
    private static String enchTag = "ench";
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

    public static boolean hasBookEnchantment(ItemStack subject, Enchantment comparison)
    {
        NBTTagList enchs = ItemEnchantedBook.getEnchantments(subject);
        boolean result = false;

        for (int i = 0; i < enchs.tagCount(); ++i)
        {
            NBTTagCompound curr = enchs.getCompoundTagAt(i);
            if(Enchantment.getEnchantmentByID(curr.getShort("id")) == comparison)
            {
                result = true;
                break;
            }
        }

        return result;
    }

    public static int enchantmentCountOf(ItemStack subject)
    {
        NBTTagCompound comp = subject.getTagCompound();
        if(comp == null) return 0;

        if(comp.hasKey(enchTag))
        {
            NBTBase closer = comp.getTag(enchTag);

            if(!(closer instanceof NBTTagList)) return 0;

            //enchs = comp.getTagList("ench", 10);

            return ((NBTTagList) closer).tagCount();
        }
        NBTTagList enchs = ItemEnchantedBook.getEnchantments(subject);
        if(enchs == null) return 0;
        return enchs.tagCount();
    }

    public static void addLore(ItemStack subject, String... lore)
    {
        NBTTagCompound nbt = subject.getTagCompound();
        if(nbt == null)
        {
            nbt = new NBTTagCompound();
            subject.setTagCompound(nbt);
        }
        NBTTagCompound display;

        if(nbt.hasKey("display"))
        {
            display = nbt.getCompoundTag("display");
        }
        else
        {
            display = new NBTTagCompound();
            nbt.setTag("display", display);
        }

        NBTTagList lore_container;
        if(display.hasKey("Lore"))
        {
            lore_container = display.getTagList("Lore", 8);

            for(int i = 0; i < lore.length; ++i)
            {
                lore_container.appendTag(new NBTTagString(lore[i]));
            }
            return;
        }

        lore_container = new NBTTagList();

        for(int i = 0; i < lore.length; ++i)
        {
            lore_container.appendTag(new NBTTagString(lore[i]));
        }

        display.setTag("Lore", lore_container);
    }
}
