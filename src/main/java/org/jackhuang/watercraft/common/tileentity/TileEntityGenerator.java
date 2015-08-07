package org.jackhuang.watercraft.common.tileentity;

import factorization.api.Charge;
import factorization.api.Coord;
import factorization.api.IChargeConductor;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.energy.tile.IKineticSource;

import java.util.Random;

import org.jackhuang.watercraft.WaterPower;
import org.jackhuang.watercraft.client.gui.IHasGui;
import org.jackhuang.watercraft.common.EnergyType;
import org.jackhuang.watercraft.util.Mods;
import org.jackhuang.watercraft.util.Utils;


/*import buildcraft.api.mj.IBatteryObject;
 import buildcraft.api.mj.MjAPI;
 import buildcraft.api.power.IPowerEmitter;
 import buildcraft.api.power.IPowerReceptor;
 import buildcraft.api.power.PowerHandler;
 import buildcraft.api.power.PowerHandler.PowerReceiver;*/
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

@InterfaceList({
    @Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = Mods.IDs.IndustrialCraft2API, striprefs = true),
    @Interface(iface = "ic2.api.energy.tile.IKineticSource", modid = Mods.IDs.IndustrialCraft2API, striprefs = true),
    @Interface(iface = "ic2.api.energy.tile.IHeatSource", modid = Mods.IDs.IndustrialCraft2API, striprefs = true),
    @Interface(iface = "cofh.api.energy.IEnergyConnection", modid = Mods.IDs.CoFHAPIEnergy),
    @Interface(iface = "factorization.api.IChargeConductor", modid = Mods.IDs.Factorization)})
public abstract class TileEntityGenerator extends TileEntityBlock implements
	IEnergySource, IHasGui, IKineticSource, IUnitChangeable,
	IEnergyConnection, IChargeConductor, IFluidHandler, IHeatSource {

    public static Random random = new Random();

    public double storage = 0.0D;
    public double maxStorage;
    public double latestOutput = 0;
    public int production;
    public boolean addedToEnergyNet = false;
    public EnergyType energyType = EnergyType.EU;
    public boolean needsToAddToEnergyNet = false;

    private boolean deadCache = true;

    private Object charge;

    public TileEntityGenerator() {
	super(0);
    }

    public TileEntityGenerator(int production, float maxStorage) {
	super(Math.round(maxStorage / 10));
	this.production = production;
	this.maxStorage = maxStorage;

	if (Mods.Factorization.isAvailable) {
	    initCharge();
	}
    }

    @Method(modid = Mods.IDs.Factorization)
    public void initCharge() {
	charge = new Charge(this);
    }

    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public void loadEnergyTile() {

	if (isServerSide()) {
	    MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));

	    this.addedToEnergyNet = true;
	}
    }

    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public void unloadEnergyTile() {
	if (isServerSide() && this.addedToEnergyNet) {
	    MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));

	    this.addedToEnergyNet = false;
	}
    }

    @Override
    public void onLoaded() {
	needsToAddToEnergyNet = true;

	// BuildCraft & Thermal Expansion integration begins
	deadCache = true;
	this.handlerCache = null;

	super.onLoaded();
    }

    @Override
    public void onUnloaded() {
	if (Mods.IndustrialCraft2.isAvailable) {
	    unloadEnergyTile();
	}

	super.onUnloaded();
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
	return energyType == EnergyType.EU;
    }

    public boolean enableUpdateEntity() {
	return isServerSide();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
	super.readFromNBT(nbttagcompound);
	this.storage = nbttagcompound.getDouble("storage");
	this.energyType = EnergyType.values()[nbttagcompound
		.getInteger("energyType")];

	if (Mods.Factorization.isAvailable && charge != null) {
	    ((Charge) charge).readFromNBT(nbttagcompound);
	}
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
	super.writeToNBT(nbttagcompound);

	nbttagcompound.setDouble("storage", this.storage);
	nbttagcompound.setInteger("energyType", energyType.ordinal());

	if (Mods.Factorization.isAvailable && charge != null) {
	    ((Charge) charge).writeToNBT(nbttagcompound);
	}
    }

    @Override
    public void readPacketData(NBTTagCompound tag) {
	super.readPacketData(tag);
	production = tag.getInteger("production");
	storage = tag.getDouble("storage");
	latestOutput = tag.getDouble("latestOutput");
	energyType = EnergyType.values()[tag.getInteger("energyType")];
    }

    @Override
    public void writePacketData(NBTTagCompound tag) {
	super.writePacketData(tag);
	tag.setDouble("storage", storage);
	tag.setInteger("production", production);
	tag.setDouble("latestOutput", latestOutput);
	tag.setInteger("energyType", energyType.ordinal());
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public double getOfferedEnergy() {
	if (energyType == EnergyType.EU) {
	    return Math.min(this.getProduction(), this.storage);
	}
	return 0;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public void drawEnergy(double amount) {
	this.storage -= amount;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public float getWrenchDropRate() {
	return 1f;
    }

    protected abstract double computeOutput(World world, int x, int y, int z);

    protected int getProduction() {
	return production;
    }

    @Override
    public void updateEntity() {
	super.updateEntity();

	if (this.storage > this.maxStorage) {
	    this.storage = this.maxStorage;
	}

	if (!isServerSide()) {
	    return;
	}

	if (Mods.IndustrialCraft2.isAvailable) {
	    loadEnergyTile();
	}

	if (Mods.Factorization.isAvailable && charge != null) {
	    ((Charge) charge).update();
	}

	pushFluidToConsumers(getFluidTankCapacity() / 40);

	if (!isRedstonePowered()) {
	    latestOutput = computeOutput(worldObj, xCoord, yCoord, zCoord);
	    if (energyType == EnergyType.EU
		    && Mods.IndustrialCraft2.isAvailable) {
		storage += latestOutput;
	    }
	    if (energyType == EnergyType.RF && Mods.CoFHAPIEnergy.isAvailable) {
		reCache();
		storage += latestOutput;
		int j = (int) Math.min(this.production, storage);
		storage -= j;
		storage += EnergyType.RF2EU(transmitEnergy((int) EnergyType
			.EU2RF(j)));
	    }
	    /*
	     * if (energyType == EnergyType.MJ &&
	     * Mods.BuildCraftPower.isAvailable) { storage += latestOutput;
	     * for(ForgeDirection d : ForgeDirection.values()) sendPower(d); }
	     */
	    if (energyType == EnergyType.Charge
		    && Mods.Factorization.isAvailable) {
		((Charge) charge).setValue((int) EnergyType
			.EU2Charge(latestOutput));
	    }
	    if (energyType == EnergyType.Water) {
		if (getFluidTank().getFluid() != null
			&& getFluidTank().getFluid().getFluid() != null
			&& getFluidTank().getFluid().getFluid().getID() != FluidRegistry.WATER
			.getID()) {
		    getFluidTank().setFluid(null);
		}
		getFluidTank().fill(
			new FluidStack(FluidRegistry.WATER,
				(int) EnergyType.EU2Water(latestOutput)), true);
	    }
	    if (energyType == EnergyType.Steam) {
		boolean outputed = false;
		if (FluidRegistry.isFluidRegistered("steam") && !outputed) {
		    Fluid f = FluidRegistry.getFluid("steam");
		    if (getFluidTank().getFluid() != null
			    && getFluidTank().getFluid().getFluid() != null
			    && getFluidTank().getFluid().getFluid().getID() != f
			    .getID()) {
			getFluidTank().setFluid(null);
		    }
		    outputed = true;
		    getFluidTank().fill(
			    new FluidStack(f,
				    (int) EnergyType.EU2Steam(latestOutput)),
			    true);
		}
		if (FluidRegistry.isFluidRegistered("ic2steam") && !outputed) {
		    Fluid f = FluidRegistry.getFluid("ic2steam");
		    if (getFluidTank().getFluid() != null
			    && getFluidTank().getFluid().getFluid() != null
			    && getFluidTank().getFluid().getFluid().getID() != f
			    .getID()) {
			getFluidTank().setFluid(null);
		    }
		    outputed = true;
		    getFluidTank().fill(
			    new FluidStack(FluidRegistry.getFluid("ic2steam"),
				    (int) EnergyType.EU2Steam(latestOutput)),
			    true);
		}
	    }
	}
    }

    public boolean isEnergyFull() {
	return storage >= maxStorage;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public int getSourceTier() {
	return 1;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public int maxrequestHeatTick(ForgeDirection directionFrom) {
	if (energyType == EnergyType.HU) {
	    return (int) (EnergyType.EU2HU(latestOutput));
	}
	return 0;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public int requestHeat(ForgeDirection directionFrom, int requestheat) {
	if (energyType == EnergyType.HU) {
	    return Math.min(requestheat, maxrequestHeatTick(directionFrom));
	}
	return 0;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public int maxrequestkineticenergyTick(ForgeDirection directionFrom) {
	if (energyType == EnergyType.KU) {
	    return (int) (EnergyType.EU2KU(latestOutput));
	}
	return 0;
    }

    @Override
    @Method(modid = Mods.IDs.IndustrialCraft2API)
    public int requestkineticenergy(ForgeDirection directionFrom,
	    int requestkineticenergy) {
	if (energyType == EnergyType.KU) {
	    return Math.min(requestkineticenergy,
		    maxrequestkineticenergyTick(directionFrom));
	}
	return 0;
    }

    @Override
    public void setUnitId(int id) {
	if (EnergyType.values()[id] != EnergyType.EU
		&& Mods.IndustrialCraft2.isAvailable) {
	    unloadEnergyTile();
	}
	energyType = EnergyType.values()[id];
    }

    @Override
    @Method(modid = Mods.IDs.CoFHAPIEnergy)
    public boolean canConnectEnergy(ForgeDirection paramForgeDirection) {
	return true;
    }

    private Object[] handlerCache;

    @Method(modid = Mods.IDs.CoFHAPIEnergy)
    protected final int transmitEnergy(int paramInt) {
	int i;
	if (this.handlerCache != null) {
	    for (i = this.handlerCache.length; i-- > 0;) {
		IEnergyHandler localIEnergyHandler = (IEnergyHandler) this.handlerCache[i];
		if (localIEnergyHandler == null) {
		    continue;
		}
		ForgeDirection localForgeDirection = ForgeDirection.VALID_DIRECTIONS[i];
		if (localIEnergyHandler.receiveEnergy(localForgeDirection,
			paramInt, true) > 0) {
		    paramInt -= localIEnergyHandler.receiveEnergy(
			    localForgeDirection, paramInt, false);
		}
		if (paramInt <= 0) {
		    return 0;
		}
	    }
	}
	return paramInt;
    }

    @Method(modid = Mods.IDs.CoFHAPIEnergy)
    private void reCache() {
	if (this.deadCache) {
	    for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
		onNeighborTileChange(this.xCoord + d.offsetX, this.yCoord
			+ d.offsetY, this.zCoord + d.offsetZ);
	    }
	    this.deadCache = false;
	}
    }

    @Override
    @Method(modid = Mods.IDs.CoFHAPIEnergy)
    public void onNeighborTileChange(int x, int y, int z) {
	TileEntity localTileEntity = this.worldObj.getTileEntity(x, y, z);

	if (x < this.xCoord) {
	    addCache(localTileEntity, ForgeDirection.EAST.ordinal());
	} else if (x > this.xCoord) {
	    addCache(localTileEntity, ForgeDirection.WEST.ordinal());
	} else if (y < this.yCoord) {
	    addCache(localTileEntity, ForgeDirection.UP.ordinal());
	} else if (y > this.yCoord) {
	    addCache(localTileEntity, ForgeDirection.DOWN.ordinal());
	} else if (z < this.zCoord) {
	    addCache(localTileEntity, ForgeDirection.NORTH.ordinal());
	} else if (z > this.zCoord) {
	    addCache(localTileEntity, ForgeDirection.SOUTH.ordinal());
	}
    }

    @Method(modid = Mods.IDs.CoFHAPIEnergy)
    private void addCache(TileEntity paramTileEntity, int dir) {
	if (this.handlerCache != null) {
	    this.handlerCache[dir] = null;
	}
	if ((paramTileEntity instanceof IEnergyHandler)) {
	    if (((IEnergyHandler) paramTileEntity)
		    .canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[dir])) {
		if (this.handlerCache == null) {
		    this.handlerCache = new IEnergyHandler[6];
		}
		this.handlerCache[dir] = ((IEnergyHandler) paramTileEntity);
	    }
	}
    }

    @Override
    public boolean isActive() {
	return !isRedstonePowered();
    }

    @Override
    @Method(modid = Mods.IDs.Factorization)
    public Charge getCharge() {
	return (Charge) charge;
    }

    @Override
    @Method(modid = Mods.IDs.Factorization)
    public Coord getCoord() {
	return new Coord(this);
    }

    public double getFromEU(double eu) {
	return energyType.getFromEU(eu);
    }

    @Override
    @Method(modid = Mods.IDs.Factorization)
    public String getInfo() {
	StringBuilder sb = new StringBuilder();
	sb.append(StatCollector.translateToLocal("cptwtrml.gui.stored") + "/" + energyType.name() + ": " + Utils.DEFAULT_DECIMAL_FORMAT.format(getFromEU(storage))
		+ "\n");
	sb.append(StatCollector.translateToLocal("cptwtrml.gui.latest_output") + "/" + energyType.name() + ": "
		+ Utils.DEFAULT_DECIMAL_FORMAT.format(getFromEU(latestOutput)) + "\n");
	return sb.toString();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
	return 0;
    }

    @Override
    public boolean canFill(ForgeDirection paramForgeDirection, Fluid paramFluid) {
	return false;
    }

    @Override
    public boolean canDrain(ForgeDirection paramForgeDirection, Fluid paramFluid) {
	return true;
    }

}
