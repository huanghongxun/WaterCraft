/**
 * Copyright (c) Huang Yuhui, 2014
 *
 * "WaterCraft" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package org.jackhuang.watercraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import org.jackhuang.watercraft.client.gui.CreativeTabWaterCraft;
import org.jackhuang.watercraft.common.CommonProxy;
import org.jackhuang.watercraft.common.block.GlobalBlocks;
import org.jackhuang.watercraft.common.block.machines.BlockMachines;
import org.jackhuang.watercraft.common.block.ore.BlockOre;
import org.jackhuang.watercraft.common.block.ore.ItemOreDust;
import org.jackhuang.watercraft.common.block.reservoir.BlockReservoir;
import org.jackhuang.watercraft.common.block.turbines.BlockTurbine;
import org.jackhuang.watercraft.common.block.watermills.BlockWatermill;
import org.jackhuang.watercraft.common.block.watermills.WaterType;
import org.jackhuang.watercraft.common.item.GlobalItems;
import org.jackhuang.watercraft.common.item.crafting.ItemCrafting;
import org.jackhuang.watercraft.common.item.crafting.ItemMaterial;
import org.jackhuang.watercraft.common.item.others.ItemOthers;
import org.jackhuang.watercraft.common.item.range.ItemPlugins;
import org.jackhuang.watercraft.common.item.range.ItemRange;
import org.jackhuang.watercraft.common.item.rotors.RotorType;
import org.jackhuang.watercraft.common.network.MessagePacketHandler;
import org.jackhuang.watercraft.common.recipe.EasyRecipeRegistrator;
import org.jackhuang.watercraft.common.recipe.IRecipeRegistrator;
import org.jackhuang.watercraft.common.recipe.NormalRecipeRegistrator;
import org.jackhuang.watercraft.integration.CraftGuideIntegration;
import org.jackhuang.watercraft.util.Mods;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jackhuang.watercraft.util.WPLog;

/**
 * WaterPower Main Class.
 *
 * @author jackhuang
 */
@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.Version,
	dependencies = "required-after:Forge@[10.13.0.1199,); required-after:IC2@[2.2.628,); after:gregtech; after:Thaumcraft@[4.2.3.5,); after:BuildCraftAPI|power[1.1,); after:NotEnoughItems; after:Forestry; after:craftguide; after:Waila; after:factorization; after:CoFHCore; after:Mekanism")
public class WaterPower implements IWorldGenerator {

    @Mod.Instance(Reference.ModID)
    public static WaterPower instance; //Create Instance

    @SidedProxy(clientSide = "org.jackhuang.watercraft.client.ClientProxy", serverSide = "org.jackhuang.watercraft.common.CommonProxy")
    public static CommonProxy proxy;   //Create CommonProxy

    public static final CreativeTabs creativeTabWaterPower = new CreativeTabWaterCraft(
	    "creativeTabWaterPower"); //Set CreativeTab.

    public static final int updateTick = 20; //Set Update Tick.

    private Configuration config;

    private IRecipeRegistrator recipe;

    /**
     * Pre Init.
     *
     * @param event
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
	config = new Configuration(event.getSuggestedConfigurationFile());
	config.load();
	Reference.initConfig(config);

	MessagePacketHandler.init();

	config.save();

    }

    /**
     * Init.
     *
     * @param event
     */
    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {

	IRecipeRegistrator.initRecipeConfig(config);
	EasyRecipeRegistrator.initRecipeConfig(config);
	NormalRecipeRegistrator.initRecipeConfig(config);

	init();

	Property enableEasyRecipe = config.get("recipe", "enableEasyRecipe", false);
	if (enableEasyRecipe.getBoolean(false)) {
	    recipe = new EasyRecipeRegistrator(config);
	}
	Property enableNormalRecipe = config.get("recipe", "enableNormalRecipe", true);
	if (enableNormalRecipe.getBoolean(true)) {
	    recipe = new NormalRecipeRegistrator(config);
	}

	GameRegistry.registerWorldGenerator(this, 0);

	recipe.registerAllRecipes();

	NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

	config.save();

	if (Mods.CraftGuide.isAvailable) {
	    CraftGuideIntegration craftGuideIntegration = new CraftGuideIntegration();
	}
    }

    /**
     * Complete Init.
     *
     * @param event
     */
    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
	proxy.loadComplete();
    }

    /**
     * Post-Init.
     *
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
	proxy.registerRenderer();
    }

    /**
     * Init.
     */
    protected void init() {

	GlobalItems.updater = new ItemOthers();

	GlobalBlocks.reservoir = new BlockReservoir();
	GlobalBlocks.machine = new BlockMachines();

	GlobalItems.crafting = new ItemCrafting();
	GlobalItems.meterial = new ItemMaterial();
	GlobalItems.oreDust = new ItemOreDust();
	GlobalItems.range = new ItemRange();
	GlobalItems.plugins = new ItemPlugins();

	RotorType.initRotors();
	RotorType.registerRotor();
	WaterType.initTrousers();

	GlobalBlocks.waterMill = new BlockWatermill();
	GlobalBlocks.turbine = new BlockTurbine();
	new BlockOre();
    }

    /**
     * Plumb is runned in server.
     *
     * @return is runned in server.
     */
    public static boolean isSimulating() {
	return !FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    /**
     * Get can-minable block.
     *
     * @param is Item Stack
     * @param number number of block
     * @return can-minable block.
     */
    private static WorldGenMinable getMinable(ItemStack is, int number) {
	return new WorldGenMinable(Block.getBlockFromItem(is.getItem()), is.getItemDamage(), number, Blocks.stone);
    }

    /**
     * Generate ore in world.
     *
     * @param ore will generate in world
     * @param number generate how many ore in a chunk
     * @param baseCount generate ores to low
     * @param world generate in what world
     * @param random random seed
     * @param chunkX
     * @param chunkZ
     * @param low low-platform to generate ores
     * @param high high-platform to generate ores
     */
    private static void generateOre(ItemStack ore, int number, int baseCount,
	    World world, Random random, int chunkX, int chunkZ, int low, int high) {
	if (ore != null) {
	    int count = (int) Math.round(random.nextGaussian() * Math.sqrt(baseCount) + baseCount);

	    for (int n = 0; n < count; n++) {
		int x = chunkX * 16 + random.nextInt(16);
		int y = random.nextInt(high - low) + low;
		int z = chunkZ * 16 + random.nextInt(16);
		getMinable(ore, number).generate(world, random, x, y, z);
	    }
	}
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
	    IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
	int baseHeight = world.provider.getAverageGroundLevel() + 1;
	int baseScale = Math.round(baseHeight * Reference.WorldGen.oreDensityFactor / 20);
	int baseCount = 15 * baseScale / 64;

	if (Reference.WorldGen.vanadiumOre) {
	    generateOre(GlobalBlocks.vanadiumOre, 2, baseCount, world, random, chunkX, chunkZ, 16, 32);
	}
	if (Reference.WorldGen.manganeseOre) {
	    generateOre(GlobalBlocks.manganeseOre, 2, baseCount, world, random, chunkX, chunkZ, 16, 32);
	}
	if (Reference.WorldGen.monaziteOre) {
	    generateOre(GlobalBlocks.monaziteOre, 2, baseCount, world, random, chunkX, chunkZ, 16, 32);
	}
	if (Reference.WorldGen.magnetOre) {
	    generateOre(GlobalBlocks.magnetOre, 10, baseCount, world, random, chunkX, chunkZ, 16, 32);
	}
	if (Reference.WorldGen.zincOre) {
	    generateOre(GlobalBlocks.zincOre, 15, baseCount, world, random, chunkX, chunkZ, 0, 64);
	}
    }

    /**
     * Get current world.
     *
     * @return current world
     */
    @SideOnly(Side.CLIENT)
    public static World getWorld() {
	Minecraft mc = FMLClientHandler.instance().getClient();
	if (mc != null) {
	    return mc.theWorld;
	}
	return null;
    }

    public static boolean isDeobf() {
	try {
	    Class c = ChunkCoordinates.class.getClassLoader().loadClass("net.minecraft.util.ChunkCoordinates");
	    Constructor co = c.getConstructor(Integer.class, Integer.class, Integer.class);
	    ChunkCoordinates cc = (ChunkCoordinates) co.newInstance(1, 2, 3);
	    Method f = c.getMethod("set");
	    f.invoke(cc, 4, 5, 6);
	    return true;
	} catch (Throwable e) {
	    WPLog.log(e.getLocalizedMessage());
	    return false;
	}
    }
}
