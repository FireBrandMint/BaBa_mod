package com.gj.bigbag.Items;

import com.gj.bigbag.Items.util.SatchelEntry;
import com.gj.bigbag.Items.util.StringIntContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Satchel extends BaseItem
{
    //READ TileEntityHopper

    static ArrayList<Integer> CacheInt = new ArrayList<Integer>(36);
    static ArrayList<Integer> CacheInt2 = new ArrayList<Integer>(36);
    final int MaxItemAmount = 1728;

    static ArrayList<SatchelEntry> ItemsStored = new ArrayList<SatchelEntry>(36);
    static int ItemAmount = 0;
    public Satchel(String name, CreativeTabs creativeTab)
    {
        super(name, creativeTab);

        this.setUnlocalizedName(name);

        this.maxStackSize = 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if(playerIn.world.isRemote || ItemAmount >= InternalMaxQuantity()) return super.onItemRightClick(worldIn, playerIn, handIn);

        ItemStack stack = playerIn.getHeldItem(handIn);

        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        DeserializeNBT(stack.getTagCompound());

        InventoryPlayer inventory = playerIn.inventory;

        String thisName = this.getRegistryName().toString();

        if(playerIn.isSneaking())
        {
            for(int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack curr = inventory.getStackInSlot(i);

                if(curr.getCount() == 0)
                {
                    continue;
                }

                StringIntContainer name = new StringIntContainer(curr.getItem().getRegistryName().toString(), curr.getMetadata());

                int where = this.FindItemIndex(name);

                if(where != -1)
                {
                    int maxStack = curr.getMaxStackSize();

                    int itemCount = curr.getCount();

                    if(itemCount < curr.getMaxStackSize())
                    {
                        int currAmount = this.GetValue(where);
                        if(itemCount + currAmount > maxStack)
                        {
                            int transfered = curr.getMaxStackSize() - itemCount;

                            curr.setCount(maxStack);

                            this.SetItem(where, currAmount - transfered);

                            ItemAmount -= transfered;
                        }
                        else
                        {
                            ItemsStored.remove(name);
                            curr.setCount(itemCount + currAmount);

                            ItemAmount -= currAmount;
                        }
                    }
                }
            }

            NonNullList<ItemStack> mainInv = inventory.mainInventory;

            for(int i = 0; i < mainInv.size(); ++i)
            {
                if(mainInv.get(i).getCount() <= 0) CacheInt.add(i);
            }

            int cacheIndex = 0;

            if(ItemsStored.size() != 0)
            for (int i = 0; i < ItemsStored.size(); ++i)
            {
                SatchelEntry entry = ItemsStored.get(i);

                if(cacheIndex == CacheInt.size()) break;

                int amount = entry.value;

                ResourceLocation resource = new ResourceLocation(entry.key.str);

                if(!Item.REGISTRY.containsKey(resource))
                {
                    //playerIn.sendMessage(new TextComponentString(entry.key.str));
                    CacheInt2.add(i);
                    continue;
                }

                ItemStack item = new ItemStack(Item.REGISTRY.getObject(resource), 1, entry.key.integer);

                int maxStack = item.getMaxStackSize();

                while(amount > 0 && cacheIndex != CacheInt.size())
                {
                    if(amount > maxStack)
                    {
                        mainInv.set(CacheInt.get(cacheIndex), new ItemStack(item.getItem(), maxStack, item.getMetadata()));
                        amount -= maxStack;
                        ++cacheIndex;

                        ItemAmount -= maxStack;

                        continue;
                    }

                    mainInv.set(CacheInt.get(cacheIndex), new ItemStack(item.getItem(), amount, item.getMetadata()));
                    ItemAmount -= amount;
                    amount = 0;

                    this.SetItem(i, 0);

                    ++cacheIndex;
                    CacheInt2.add(i);

                    break;
                }

                if(amount != 0) this.SetItem(i, amount);
            }

            for(int i = CacheInt2.size() - 1; i >= 0; --i)
            {
                ItemsStored.remove(CacheInt2.get(i));
            }

            SerializeNBT(stack.getTagCompound());
            FlushAll();
            inventory.markDirty();

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }

        int i = 0;
        while(i < inventory.getSizeInventory())
        {
            //if(AddToVirtualBag(i, thisName, inventory, playerIn)) break;
            AddToVirtualBag(i, thisName, inventory, playerIn);

            ++i;
        }

        SerializeNBT(stack.getTagCompound());
        FlushAll();
        playerIn.inventory.markDirty();

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    public int InternalMaxQuantity()
    {
        return MaxItemAmount;
    }

    public boolean InternalItemFitsInSatchel(ItemStack itemstack, StringIntContainer name)
    {
        return true;
    }

    protected boolean IsStackClean(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) return true;

        return false;
    }

    public NBTTagCompound SerializeNBT(NBTTagCompound nbt) {
        NBTTagCompound nbt2 = new NBTTagCompound();

        for (int i = 0; i < ItemsStored.size(); ++i)
        {
            SatchelEntry entry = ItemsStored.get(i);

            nbt2.setInteger(entry.key.toString(), entry.value);
        }

        nbt.setTag("items", nbt2);

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
        display = nbt.getCompoundTag("display");

        NBTTagList lore = new NBTTagList();

        lore.appendTag(new NBTTagString(this.ItemAmount + "\\" + InternalMaxQuantity()));
        lore.appendTag(new NBTTagString("Rmb to store things in it,"));
        lore.appendTag(new NBTTagString("shift + rmb to empty what you can."));

        display.setTag("Lore", lore);

        return nbt;
    }

    public void DeserializeNBT(NBTTagCompound nbt) {
        if(!nbt.hasKey("items")) return;
        NBTTagCompound nbt2 = (NBTTagCompound) nbt.getTag("items");

        for (final String key : nbt2.getKeySet())
        {
            int amount = nbt2.getInteger(key);

            this.SetItem(StringIntContainer.Parse(key), amount);

            ItemAmount += amount;
        }
    }

    public boolean AddToVirtualBag (int i, String thisName, InventoryPlayer inventory, EntityPlayer playerIn)
    {
        ItemStack curr = inventory.getStackInSlot(i);

        StringIntContainer name = new StringIntContainer(curr.getItem().getRegistryName().toString(), curr.getMetadata());

        if(curr.getTagCompound() != null || curr.getCount() == 0 || name.str.equals(thisName) || !InternalItemFitsInSatchel(curr, name))
            return false;

        int where = this.FindItemIndex(name);

        int transfered = curr.getCount() + ItemAmount > InternalMaxQuantity() ? InternalMaxQuantity() - ItemAmount : curr.getCount();
        if(where != -1) this.SetItem(where, transfered + this.GetValue(where));
        else this.SetItem(name, transfered);


        curr.setCount(curr.getCount() - transfered);
        this.ItemAmount += transfered;

        //PrintNBT(curr, playerIn);

        return true;
    }

    int FindItemIndex(StringIntContainer subject)
    {
        int result = -1;

        for(int i = 0; i < ItemsStored.size(); ++i)
        {
            if(ItemsStored.get(i).key.equals(subject))
            {
                result = i;
                break;
            }
        }

        return result;
    }

    void SetItem(int index, int value)
    {
        ItemsStored.get(index).value = value;
    }

    void SetItem (StringIntContainer key, int value)
    {
        ItemsStored.add(new SatchelEntry(key, value));
    }

    int GetValue(int index)
    {
        return ItemsStored.get(index).value;
    }

    void FlushAll()
    {
        ItemAmount = 0;
        CacheInt.clear();
        CacheInt2.clear();
        ItemsStored.clear();
    }

    void PrintNBT(ItemStack stack, EntityPlayer player)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) player.sendMessage(new TextComponentString("null"));
        else
        {
            player.sendMessage(new TextComponentString(compound.toString()));
        }
    }
}
