/**
 * Copyright (c) Huang Yuhui, 2014
 *
 * "WaterCraft" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package org.jackhuang.watercraft.common.inventory;

import net.minecraft.item.ItemStack;

import org.jackhuang.watercraft.common.tileentity.TileEntityInventory;
import org.jackhuang.watercraft.api.IUpgrade;

public class InventorySlotUpgrade extends InventorySlot {

    public InventorySlotUpgrade(TileEntityInventory base, String name,
	    int count) {
	super(base, name, InventorySlot.Access.NONE, count);
    }

    public boolean accepts(ItemStack itemStack) {
	return itemStack.getItem() instanceof IUpgrade;
    }
}
