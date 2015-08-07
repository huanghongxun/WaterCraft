package org.jackhuang.watercraft.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import org.jackhuang.watercraft.common.inventory.InventorySlot;
import org.jackhuang.watercraft.common.inventory.InventorySlot.InvSide;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityInventory extends TileEntityBase implements
	ISidedInventory {

    public final List<InventorySlot> invSlots = new ArrayList<InventorySlot>();

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
	super.readFromNBT(nbtTagCompound);

	NBTTagCompound invSlotsTag = nbtTagCompound.getCompoundTag("InvSlots");

	for (InventorySlot invSlot : this.invSlots) {
	    invSlot.readFromNBT(invSlotsTag.getCompoundTag(invSlot.name));
	}
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound) {
	super.writeToNBT(nbtTagCompound);

	NBTTagCompound invSlotsTag = new NBTTagCompound();

	for (InventorySlot invSlot : this.invSlots) {
	    NBTTagCompound invSlotTag = new NBTTagCompound();

	    invSlot.writeToNBT(invSlotTag);

	    invSlotsTag.setTag(invSlot.name, invSlotTag);
	}

	nbtTagCompound.setTag("InvSlots", invSlotsTag);
    }

    @Override
    public boolean hasCustomInventoryName() {
	return true;
    }

    public int getSizeInventory() {
	int ret = 0;

	for (InventorySlot invSlot : this.invSlots) {
	    ret += invSlot.size();
	}

	return ret;
    }

    public ItemStack getStackInSlot(int index) {
	for (InventorySlot invSlot : this.invSlots) {
	    if (index < invSlot.size()) {
		return invSlot.get(index);
	    }
	    index -= invSlot.size();
	}

	return null;
    }

    public ItemStack decrStackSize(int index, int amount) {
	ItemStack itemStack = getStackInSlot(index);
	if (itemStack == null) {
	    return null;
	}

	if (amount >= itemStack.stackSize) {
	    setInventorySlotContents(index, null);

	    return itemStack;
	}

	itemStack.stackSize -= amount;

	ItemStack ret = itemStack.copy();
	ret.stackSize = amount;

	return ret;
    }

    public ItemStack getStackInSlotOnClosing(int index) {
	ItemStack ret = getStackInSlot(index);

	if (ret != null) {
	    setInventorySlotContents(index, null);
	}

	return ret;
    }

    public void setInventorySlotContents(int index, ItemStack itemStack) {
	for (InventorySlot invSlot : this.invSlots) {
	    if (index < invSlot.size()) {
		invSlot.put(index, itemStack);
		break;
	    }
	    index -= invSlot.size();
	}
    }

    public boolean isInvNameLocalized() {
	return false;
    }

    public int getInventoryStackLimit() {
	return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
	return entityPlayer.getDistance(this.xCoord + 0.5D, this.yCoord + 0.5D,
		this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int index, ItemStack itemStack) {
	InventorySlot invSlot = getInvSlot(index);

	return (invSlot != null) && (invSlot.canInput())
		&& (invSlot.accepts(itemStack));
    }

    public int[] getAccessibleSlotsFromSide(int var1) {
	int[] ret = new int[getSizeInventory()];

	for (int i = 0; i < ret.length; i++) {
	    ret[i] = i;
	}

	return ret;
    }

    public boolean canInsertItem(int index, ItemStack itemStack, int side) {
	InventorySlot targetSlot = getInvSlot(index);
	if (targetSlot == null) {
	    return false;
	}

	if ((!targetSlot.canInput()) || (!targetSlot.accepts(itemStack))) {
	    return false;
	}
	if ((targetSlot.preferredSide != InventorySlot.InvSide.ANY)
		&& (targetSlot.preferredSide.matches(side))) {
	    return true;
	}

	for (InventorySlot invSlot : this.invSlots) {
	    if ((invSlot != targetSlot)
		    && (invSlot.preferredSide != InventorySlot.InvSide.ANY)
		    && (invSlot.preferredSide.matches(side))
		    && (invSlot.canInput()) && (invSlot.accepts(itemStack))) {
		return false;
	    }
	}

	return true;
    }

    public boolean canExtractItem(int index, ItemStack itemStack, int side) {
	InventorySlot targetSlot = getInvSlot(index);
	if (targetSlot == null) {
	    return false;
	}

	if (!targetSlot.canOutput()) {
	    return false;
	}
	if ((targetSlot.preferredSide != InventorySlot.InvSide.ANY)
		&& (targetSlot.preferredSide.matches(side))) {
	    return true;
	}

	for (InventorySlot invSlot : this.invSlots) {
	    if ((invSlot != targetSlot)
		    && (invSlot.preferredSide != InventorySlot.InvSide.ANY)
		    && (invSlot.preferredSide.matches(side))
		    && (invSlot.canOutput())) {
		return false;
	    }
	}

	return true;
    }

    public void addInvSlot(InventorySlot invSlot) {
	this.invSlots.add(invSlot);
    }

    private InventorySlot getInvSlot(int index) {
	for (InventorySlot invSlot : this.invSlots) {
	    if (index < invSlot.size()) {
		return invSlot;
	    }
	    index -= invSlot.size();
	}

	return null;
    }

}
