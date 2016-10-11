/**
 * Copyright (c) Huang Yuhui, 2014
 * 
 * "WaterCraft" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package waterpower.common.block.machines;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import waterpower.api.IWaterReceiver;
import waterpower.common.block.tileentity.TileEntityBlock;

public abstract class TileEntityWaterMachine extends TileEntityBlock implements IWaterReceiver {
    public TileEntityWaterMachine(int maxWater) {
        super(maxWater);
    }

    @Override
    public int canProvideWater(int water, EnumFacing side, TileEntity provider) {
        int need = getFluidTankCapacity() - getFluidAmount();
        need = Math.min(need, water);
        return need;
    }

    @Override
    public void provideWater(int provide) {
        this.getFluidTank().fill(new FluidStack(FluidRegistry.WATER, provide), true);
    }
}