package org.jackhuang.watercraft.common.block.reservoir;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jackhuang.watercraft.Reference;
import org.jackhuang.watercraft.common.block.BlockMeta;
import org.jackhuang.watercraft.common.item.others.ItemType;
import org.jackhuang.watercraft.common.recipe.IRecipeRegistrar;
import org.jackhuang.watercraft.integration.ic2.ICItemFinder;
import org.jackhuang.watercraft.util.Mods;

import thaumcraft.api.ItemApi;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockReservoir extends BlockMeta {

    public BlockReservoir() {
	super("cptBlockReservoir", Material.iron, ItemReservoir.class);

	registerReservoir();

	GameRegistry.registerTileEntity(TileEntityReservoir.class,
		"cptwtrml.reservoir");
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
	textures = new IIcon[maxMetaData()][6];
	for (int i = 0; i < maxMetaData(); i++) {
	    textures[i][0] = textures[i][1] = textures[i][2] = textures[i][3] = textures[i][4] = textures[i][5] = iconRegister
		    .registerIcon(Reference.ModID + ":reservoir/"
			    + ReservoirType.values()[i].name());
	}
    }

    @Override
    protected int getTextureIndex(IBlockAccess iBlockAccess, int x, int y,
	    int z, int meta) {
	TileEntity tTileEntity = iBlockAccess.getTileEntity(x, y, z);
	if (tTileEntity instanceof TileEntityReservoir) {
	    TileEntityReservoir te = (TileEntityReservoir) tTileEntity;
	    if (te.type != null) {
		return te.type.ordinal();
	    }
	}
	return meta;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2) {
	return ReservoirType.makeTileEntity(0);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
	return ReservoirType.makeTileEntity(metadata);
    }

    @Override
    protected String getTextureFolder(int index) {
	return "reservoir";
    }

    @Override
    protected int maxMetaData() {
	return ReservoirType.values().length;
    }

    public ArrayList<String> getDebugInfo(EntityPlayer aPlayer, int aX, int aY,
	    int aZ, int aLogLevel) {
	ArrayList<String> al = new ArrayList<String>();
	TileEntity tileEntity = aPlayer.worldObj.getTileEntity(aX, aY, aZ);
	if (tileEntity instanceof TileEntityReservoir) {
	    TileEntityReservoir te = (TileEntityReservoir) tileEntity;
	    if (te.type == null) {
		al.add("Type: null");
	    } else {
		al.add("Type: " + te.type.name());
	    }
	    al.add("Water: " + te.getFluidAmount());
	} else {
	    al.add("Not a reservoir tile entity.");
	}
	return al;
    }

    public void registerReservoir() {

	// Reservoir recipes registering
	addReservoirRecipe(new ItemStack(this, 8, 0), "logWood");
	addReservoirRecipe(new ItemStack(this, 8, 1), Blocks.stone);
	addReservoirRecipe(new ItemStack(this, 8, 2), Blocks.lapis_block);
	addReservoirRecipe(new ItemStack(this, 8, 3), "blockTin");
	addReservoirRecipe(new ItemStack(this, 8, 4), "blockCopper");
	addReservoirRecipe(new ItemStack(this, 8, 5), "blockLead");
	addReservoirRecipe(new ItemStack(this, 8, 6), Blocks.quartz_block);
	addReservoirRecipe(new ItemStack(this, 8, 7), "blockBronze");
	addReservoirRecipe(new ItemStack(this, 8, 8), Blocks.iron_block);
	addReservoirRecipe(new ItemStack(this, 8, 9), Blocks.nether_brick);
	addReservoirRecipe(new ItemStack(this, 8, 10), Blocks.obsidian);
	addReservoirRecipe(new ItemStack(this, 8, 11), "blockSilver");
	addReservoirRecipe(new ItemStack(this, 8, 12), Blocks.gold_block);
	addReservoirRecipe(new ItemStack(this, 8, 13),
		ICItemFinder.getIC2Item("carbonPlate"));
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 14),
		ICItemFinder.getIC2Item("advancedAlloy"));
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 15),
		Blocks.emerald_block);
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 16),
		Blocks.diamond_block);
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 17),
		ICItemFinder.getIC2Item("iridiumOre"));
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 18),
		ICItemFinder.getIC2Item("iridiumPlate"));
	addReservoirRecipe(new ItemStack(this, 8, 19), "blockZinc");
	addReservoirRecipe(new ItemStack(this, 8, 20), "blockBrass");
	addReservoirRecipe(new ItemStack(this, 8, 21),
		"blockAluminum");
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 22),
		"blockSteel");
	addReservoirRecipe(new ItemStack(this, 8, 23), "blockInvar");
	addReservoirRecipe(new ItemStack(this, 8, 24),
		"blockElectrum");
	addReservoirRecipe(new ItemStack(this, 8, 25),
		"blockNickel");
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 26),
		"blockOsmium");
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 27),
		"blockTitanium");
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 28),
		"blockPlatinum");
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 29),
		"blockTungsten");
	addReservoirAdvancedRecipe(new ItemStack(this, 8, 30),
		"blockChrome");
	addReservoirAdvancedRecipe(new ItemStack(this, 8,
		31), "blockTungstenSteel");
	if (Mods.Thaumcraft.isAvailable) {
	    addReservoirAdvancedRecipe(new ItemStack(this, 8, 32),
		    ItemApi.getBlock("blockCosmeticSolid", 4));
	}
    }

    void addReservoirRecipe(ItemStack output, Object S) {
	if (S == null) {
	    return;
	}
	IRecipeRegistrar.addRecipeByOreDictionary(output, "SSS", "SIS", "SSS",
		'S', S, 'I', ItemType.ReservoirCore.item());
    }

    void addReservoirAdvancedRecipe(ItemStack output, Object S) {
	if (S == null) {
	    return;
	}
	IRecipeRegistrar.addRecipeByOreDictionary(output, "SSS", "SIS", "SSS",
		'S', S, 'I', ItemType.ReservoirCoreAdvanced.item());
    }

}
