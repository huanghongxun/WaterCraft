package org.jackhuang.watercraft.integration.waila;

import java.util.List;

import org.jackhuang.watercraft.common.block.reservoir.TileEntityReservoir;
import org.jackhuang.watercraft.util.Mods;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@InterfaceList({
    @Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = Mods.IDs.Waila)
})
public class HUDHandlerReservoir implements IWailaDataProvider {

    @Override
    @Method(modid = Mods.IDs.Waila)
    public List<String> getWailaBody(ItemStack arg0, List<String> arg1,
	    IWailaDataAccessor arg2, IWailaConfigHandler arg3) {
	TileEntity te = arg2.getTileEntity();
	if (!(te instanceof TileEntityReservoir)) {
	    return arg1;
	}
	TileEntityReservoir tile = (TileEntityReservoir) te;
	arg1.add(StatCollector.translateToLocal("cptwtrml.gui.capacity") + ": " + tile.getMaxFluidAmount());
	arg1.add(StatCollector.translateToLocal("cptwtrml.gui.reservoir.add") + ": " + tile.getLastAddedWater());
	if (tile.getFluidTank() == null) {
	    return arg1;
	}
	FluidStack f = tile.getFluidTank().getFluid();
	arg1.add(StatCollector.translateToLocal("cptwtrml.gui.stored_fluid") + ": " + (f == null ? StatCollector.translateToLocal("cptwtrml.gui.empty") : f.getLocalizedName()));
	arg1.add(StatCollector.translateToLocal("cptwtrml.gui.fluid_amount") + ": " + tile.getFluidAmount());
	arg1.add(StatCollector.translateToLocal("cptwtrml.gui.reservoir.hpWater") + ": " + tile.getHPWater());
	return arg1;
    }

    @Override
    @Method(modid = Mods.IDs.Waila)
    public List<String> getWailaHead(ItemStack arg0, List<String> arg1,
	    IWailaDataAccessor arg2, IWailaConfigHandler arg3) {
	TileEntity te = arg2.getTileEntity();
	if (!(te instanceof TileEntityReservoir)) {
	    return arg1;
	}
	TileEntityReservoir tile = (TileEntityReservoir) te;
	arg1.set(0, tile.type.getShowedName());
	return arg1;
    }

    @Override
    @Method(modid = Mods.IDs.Waila)
    public ItemStack getWailaStack(IWailaDataAccessor arg0,
	    IWailaConfigHandler arg1) {
	return null;
    }

    @Override
    @Method(modid = Mods.IDs.Waila)
    public List<String> getWailaTail(ItemStack arg0, List<String> arg1,
	    IWailaDataAccessor arg2, IWailaConfigHandler arg3) {
	return arg1;
    }

    @Override
    @Method(modid = Mods.IDs.Waila)
    public NBTTagCompound getNBTData(EntityPlayerMP arg0, TileEntity arg1,
	    NBTTagCompound arg2, World arg3, int arg4, int arg5, int arg6) {
	return null;
    }

}
