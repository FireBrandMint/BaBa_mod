package com.gj.baba.blocks.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

public class ExtractionChestInventory {

    static String errorNBT = "It can't store modified items.";
    static String errorFull = "It's full!";
    public static String saveTag = "ExtractionChestData";
    public static final int maxSlots = 60;
    public static final int maxPerDelivery = 5;
    public static final int cooldownSeconds = 300;
    int slotsCount;
    private NonNullList<ItemStack> inventoryContents;
    int timesUsed;
    long lastFullUseMillis;

    public ExtractionChestInventory(int slotsCount)
    {
        this.slotsCount = slotsCount;
        this.inventoryContents = NonNullList.<ItemStack>withSize(slotsCount, ItemStack.EMPTY);

        timesUsed = 0;
        lastFullUseMillis = System.currentTimeMillis() - cooldownSeconds * 1000L;
    }

    public ExtractionChestInventory(NBTTagCompound source)
    {
        readNBT(source);
    }

    public ItemStack addItem(ItemStack stack, EntityPlayer player)
    {
        if(stack.hasTagCompound())
        {
            player.sendMessage(new TextComponentString(errorNBT));
            return stack;
        }

        long timePassed = Math.abs(System.currentTimeMillis() - lastFullUseMillis);
        if(timePassed < cooldownSeconds * 1000L)
        {
            long nextTime = (cooldownSeconds * 1000L - timePassed) / 1000L;
            String msg = "You already inserted " + maxPerDelivery + " items for extraction. You'll be able to do it again in " + nextTime + " seconds.";
            player.sendMessage(new TextComponentString(msg));
            return stack;
        }

        ItemStack itemstack = stack.copy();

        boolean added = false;

        for (int i = 0; i < this.slotsCount + 1; ++i)
        {
            ItemStack itemstack1 = this.getStackInSlot(i);

            if (itemstack1.isEmpty())
            {
                this.setInventorySlotContents(i, itemstack);
                this.addNumInserted();
                return ItemStack.EMPTY;
            }

            if (ItemStack.areItemsEqual(itemstack1, itemstack))
            {
                int j = Math.min(64, itemstack1.getMaxStackSize());
                int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());

                if (k > 0)
                {
                    itemstack1.grow(k);
                    itemstack.shrink(k);

                    added = true;

                    if (itemstack.isEmpty())
                    {
                        this.addNumInserted();
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if(this.InsertNewSlot(itemstack))
        {
            this.addNumInserted();
            return ItemStack.EMPTY;
        }
        else
        {
            player.sendMessage(new TextComponentString(errorFull));
        }

        if(added) this.addNumInserted();

        return itemstack;
    }

    private boolean InsertNewSlot(ItemStack stack)
    {
        if(slotsCount + 1 > maxSlots) return false;
        NonNullList<ItemStack> old = inventoryContents;
        NonNullList<ItemStack> now = NonNullList.<ItemStack>withSize(slotsCount + 1, ItemStack.EMPTY);
        for(int i = 0; i < slotsCount; ++i)
        {
            now.set(i, old.get(i));
        }
        now.set(slotsCount, stack);

        slotsCount += 1;
        inventoryContents = now;

        return true;
    }

    private void addNumInserted()
    {
        long current = System.currentTimeMillis();
        long timePassed = Math.abs(current - lastFullUseMillis);
        if(timePassed > cooldownSeconds * 2000L)
        {
            lastFullUseMillis = current - cooldownSeconds * 1000L;
            timesUsed= 0;
        }
        ++timesUsed;
        if(timesUsed >= maxPerDelivery)
        {
            timesUsed = 0;
            lastFullUseMillis = System.currentTimeMillis();
        }
    }

    public ItemStack popItem()
    {
        ItemStack result = null;

        for (int i = this.slotsCount - 1; i > -1; --i)
        {
            ItemStack itemstack1 = this.getStackInSlot(i);

            if(itemstack1.isEmpty()) continue;

            result = itemstack1;
            this.setInventorySlotContents(i, ItemStack.EMPTY);
            break;
        }

        return result;
    }

    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < this.inventoryContents.size() ? (ItemStack)this.inventoryContents.get(index) : ItemStack.EMPTY;
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventoryContents.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > 64) {
            stack.setCount(64);
        }
    }

    public void writeNBT(NBTTagCompound destination)
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound inventory = new NBTTagCompound();
        ItemStackHelper.saveAllItems(inventory, inventoryContents);

        compound.setTag("inventory", inventory);
        compound.setInteger("count", slotsCount);
        compound.setInteger("uses", timesUsed);
        compound.setLong("lastfulluse", lastFullUseMillis);

        destination.setTag(saveTag, compound);
    }

    public void readNBT(NBTTagCompound source)
    {
        NBTTagCompound subject = source.getCompoundTag(saveTag);
        slotsCount = subject.getInteger("count");
        timesUsed = subject.getInteger("uses");
        lastFullUseMillis = subject.getLong("lastfulluse");
        this.inventoryContents = NonNullList.<ItemStack>withSize(slotsCount, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(subject.getCompoundTag("inventory"), inventoryContents);
    }

}
