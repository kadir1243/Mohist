/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public class RecipeWrapper implements IInventory {

    protected final IItemHandlerModifiable inv;

    public RecipeWrapper(IItemHandlerModifiable inv)
    {
        this.inv = inv;
    }

    /**
     * Returns the size of this inventory.  Must be equivalent to {@link #getHeight()} * {@link #getWidth()}.
     */
    @Override
    public int getContainerSize()
    {
        return inv.getSlots();
    }

    /**
     * Returns the stack in this slot.  This stack should be a modifiable reference, not a copy of a stack in your inventory.
     */
    @Override
    public ItemStack getItem(int slot)
    {
        return inv.getStackInSlot(slot);
    }

    /**
     * Attempts to remove n items from the specified slot.  Returns the split stack that was removed.  Modifies the inventory.
     */
    @Override
    public ItemStack removeItem(int slot, int count)
    {
        ItemStack stack = inv.getStackInSlot(slot);
        return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
    }

    /**
     * Sets the contents of this slot to the provided stack.
     */
    @Override
    public void setItem(int slot, ItemStack stack)
    {
        inv.setStackInSlot(slot, stack);
    }

    /**
     * Removes the stack contained in this slot from the underlying handler, and returns it.
     */
    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        ItemStack s = getItem(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setItem(index, ItemStack.EMPTY);
        return s;
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < inv.getSlots(); i++)
        {
            if(!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return inv.isItemValid(slot, stack);
    }

    @Override
    public List<ItemStack> getContents() {
        return null;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {

    }

    @Override
    public void onClose(CraftHumanEntity who) {

    }

    @Override
    public List<HumanEntity> getViewers() {
        return null;
    }

    @Override
    public InventoryHolder getOwner() {
        return null;
    }

    @Override
    public void setMaxStackSize(int size) {

    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void clearContent()
    {
        for(int i = 0; i < inv.getSlots(); i++)
        {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    //The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
    @Override
    public int getMaxStackSize() { return 0; }
    @Override
    public void setChanged() {}
    @Override
    public boolean stillValid(PlayerEntity player) { return false; }
    @Override
    public void startOpen(PlayerEntity player) {}
    @Override
    public void stopOpen(PlayerEntity player) {}

}
