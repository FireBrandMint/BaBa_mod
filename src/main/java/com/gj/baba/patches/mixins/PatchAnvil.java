package com.gj.baba.patches.mixins;

import com.gj.baba.BaBa;
import com.gj.baba.patches.util.PatchObjectHolder;
import com.gj.baba.util.BabaUtil;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
@Mixin(ContainerRepair.class)
public abstract class PatchAnvil extends Container
{
    @Shadow @Final private IInventory outputSlot;

    @Shadow public int materialCost;

    @Shadow public int maximumCost;

    @Shadow @Final private IInventory inputSlots;

    @Inject(method = "updateRepairOutput", at = @At("HEAD"), cancellable = true)
    public void updateRepairOutput(CallbackInfo ci)
    {
        ItemStack stack1 = this.inputSlots.getStackInSlot(0);
        ItemStack stack2 = this.inputSlots.getStackInSlot(1);
        if(stack1.isEmpty() | stack2.isEmpty()) return;

        int enchantCount1 = BabaUtil.enchantmentCountOf(stack1);

        if(
                enchantCount1 == 0
                && stack1.getItem() != Items.ENCHANTED_BOOK
                && stack2.getItem() == Items.ENCHANTED_BOOK
                && stack2.getTagCompound() != null
            )
            return;

        if(
                (stack2.getItem() == Items.ENCHANTED_BOOK || stack1.getItem() == stack2.getItem())
                && enchantCount1 + BabaUtil.enchantmentCountOf(stack2) > 2
        )
        {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.materialCost = 0;
            this.maximumCost = 40;
            ci.cancel();
        }
    }
}
