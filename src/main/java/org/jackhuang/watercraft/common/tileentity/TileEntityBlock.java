package org.jackhuang.watercraft.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;

import java.util.List;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

public abstract class TileEntityBlock extends TileEntityLiquidTankInventory implements IWrenchable {

    public TileEntityBlock(int tanksize) {
	super(tanksize);
    }

    private short facing = 0;

    public boolean prevActive = false;
    private short prevFacing = 0;

    @SideOnly(Side.CLIENT)
    private IIcon[] lastRenderIcons;

    public void readFromNBT(NBTTagCompound nbttagcompound) {
	super.readFromNBT(nbttagcompound);

	this.prevFacing = this.facing = nbttagcompound.getShort("facing");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
	super.writeToNBT(nbttagcompound);

	nbttagcompound.setShort("facing", this.facing);
    }

    @Override
    public void readPacketData(NBTTagCompound tag) {
	super.readPacketData(tag);

	this.prevFacing = this.facing = tag.getShort("facing");
    }

    @Override
    public void writePacketData(NBTTagCompound tag) {
	super.writePacketData(tag);

	tag.setShort("facing", this.facing);
    }

    @SideOnly(Side.CLIENT)
    public void onRender() {
	Block block = getBlockType();

	if (this.lastRenderIcons == null) {
	    this.lastRenderIcons = new IIcon[6];
	}

	for (int side = 0; side < 6; side++) {
	    this.lastRenderIcons[side] = block.getIcon(this.worldObj,
		    this.xCoord, this.yCoord, this.zCoord, side);
	}
    }

    public short getFacing() {
	return this.facing;
    }

    public short getPrevFacing() {
	return this.prevFacing;
    }

    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
	return facing != side;
    }

    public void setFacing(short facing) {
	this.facing = facing;

	if (this.prevFacing != facing) {
	    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	this.prevFacing = facing;
    }

    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
	return true;
    }

    public float getWrenchDropRate() {
	return 1.0F;
    }

    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
	return new ItemStack(this.worldObj.getBlock(this.xCoord, this.yCoord,
		this.zCoord), 1, this.worldObj.getBlockMetadata(this.xCoord,
			this.yCoord, this.zCoord));
    }

    public void onBlockBreak(int id, int meta) {
    }

    @Override
    public boolean canDrain(ForgeDirection paramForgeDirection, Fluid paramFluid) {
	return false;
    }

    @Override
    public boolean canFill(ForgeDirection paramForgeDirection, Fluid paramFluid) {
	return false;
    }
}
