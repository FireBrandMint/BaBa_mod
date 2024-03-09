package com.gj.baba.patches.mixins;

import com.gj.baba.BaBa;
import com.gj.baba.patches.util.PatchObjectHolder;
import com.gj.baba.util.BabaUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EntityVillager.ListEnchantedBookForEmeralds.class)
public abstract class PatchVillagerBooks implements EntityVillager.ITradeList
{

    @Inject(method = "addMerchantRecipe", at = @At(value = "RETURN"))
    public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random, CallbackInfo ci)
    {
        int justAdded = recipeList.size() - 1;
        ItemStack sellItem = recipeList.get(justAdded).getItemToSell();
        //inside of this if: switch mending book with a special one
        if(BabaUtil.hasBookEnchantment(sellItem, Enchantments.MENDING))
        {
            //pick from my custom table
            Enchantment[][] table = PatchObjectHolder.getMendingSubstitutes();
            Enchantment[] substitute = table[random.nextInt(table.length)];
            //get enchanted book from first enchantment
            Enchantment curr = substitute[0];
            int min = curr.getMinLevel();
            int max = curr.getMaxLevel();
            int dist = max - min;
            int halfdist;
            ItemStack toSell =
                    ItemEnchantedBook.getEnchantedItemStack(
                            new EnchantmentData(curr, dist == 0 ? min : min + (random.nextInt(dist)))
                    );
            //add the rest of the enchantments
            for(int i = 1; i < substitute.length; ++i)
            {
                curr = substitute[i];
                min = curr.getMinLevel();
                max = curr.getMaxLevel();
                dist = max - min;
                halfdist = dist >> 1;
                ItemEnchantedBook.addEnchantment(
                        toSell,
                        new EnchantmentData(curr, dist == 0 ? min : (min + halfdist + random.nextInt(dist - halfdist)))
                );
            }

            MerchantRecipe toReplace = new MerchantRecipe(
                    new ItemStack(Items.BOOK),
                    new ItemStack(Items.EMERALD, 35 + random.nextInt(16)),
                    toSell
            );

            recipeList.set(justAdded, toReplace);
        }
    }
}
