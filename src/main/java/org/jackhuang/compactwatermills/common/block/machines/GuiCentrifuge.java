package org.jackhuang.compactwatermills.common.block.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.jackhuang.compactwatermills.Reference;
import org.jackhuang.compactwatermills.client.gui.ContainerStandardMachine;
import org.jackhuang.compactwatermills.client.gui.GuiMachineBase;
import org.jackhuang.compactwatermills.common.tileentity.TileEntityStandardWaterMachine;

public class GuiCentrifuge extends GuiMachineBase {
	public GuiCentrifuge(EntityPlayer player,
			TileEntityCentrifuge tileEntity) {
		super(new ContainerCentrifuge(player, tileEntity));

		this.name = StatCollector
				.translateToLocal("cptwtrml.machine.centrifuge.name");
		this.inv = StatCollector.translateToLocal("container.inventory");
		this.background = new ResourceLocation(Reference.ModID + ":textures/gui/GUICentrifuge.png");
	}
}