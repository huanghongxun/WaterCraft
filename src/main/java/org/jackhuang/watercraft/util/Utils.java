/**
 * Copyright (c) Huang Yuhui, 2014
 *
 * "WaterCraft" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package org.jackhuang.watercraft.util;

import ic2.api.item.IC2Items;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class Utils {

    public static <T> T[] concat(T[] first, T[] second) {
	T[] result = Arrays.copyOf(first, first.length + second.length);
	System.arraycopy(second, 0, result, first.length, second.length);
	return result;
    }

    public static int[] concat(int[] first, int[] second) {
	int[] result = Arrays.copyOf(first, first.length + second.length);
	System.arraycopy(second, 0, result, first.length, second.length);
	return result;
    }

    public static float[] concat(float[] first, float[] second) {
	float[] result = Arrays.copyOf(first, first.length + second.length);
	System.arraycopy(second, 0, result, first.length, second.length);
	return result;
    }

    public static int[] moveForward(World world, int xCoord, int yCoord,
	    int zCoord, short facing, int step) {
	int x = xCoord;
	int y = yCoord;
	int z = zCoord;

	switch (facing) {
	    case 1:
		y += step;
		break;
	    case 2:
		y -= step;
		break;
	    case 3:
		z += step;
		break;
	    case 4:
		z -= step;
		break;
	    case 5:
		x += step;
		break;
	    case 6:
		x -= step;
		break;
	}

	return new int[]{x, y, z};
    }

    public static boolean isITNT(ItemStack is) {
	ItemStack b = IC2Items.getItem("industrialTnt");
	if (b.getItem() == is.getItem() && b.getItemDamage() == is.getItemDamage()) {
	    return true;
	} else {
	    return false;
	}
    }

    // 0 - weather, 1 - biomeID, 2 - biomeGet, 3 - biomePut
    public static double[] getBiomeRaining(World worldObj, int xCoord,
	    int zCoord) {
	int weather = worldObj.isThundering() ? 2 : worldObj.isRaining() ? 1
		: 0;
	int biomeID = worldObj.getBiomeGenForCoords(xCoord, zCoord).biomeID;
	double biomeGet = 0, biomePut = 0;
	if (biomeID == BiomeGenBase.beach.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.forest.biomeID) {
	    biomeGet = 1;
	    biomePut = 1;
	} else if (biomeID == BiomeGenBase.river.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.forestHills.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.extremeHills.biomeID) {
	    biomeGet = 0.75;
	    biomePut = 1;
	} else if (biomeID == BiomeGenBase.extremeHillsEdge.biomeID) {
	    biomeGet = 0.75;
	    biomePut = 1;
	} else if (biomeID == BiomeGenBase.ocean.biomeID) {
	    biomeGet = 1.2;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.plains.biomeID) {
	    biomeGet = 0.75;
	    biomePut = 1;
	} else if (biomeID == BiomeGenBase.mushroomIsland.biomeID) {
	    biomeGet = 1.2;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.mushroomIslandShore.biomeID) {
	    biomeGet = 1.2;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.desert.biomeID) {
	    biomeGet = 0;
	    biomePut = 4;
	} else if (biomeID == BiomeGenBase.desertHills.biomeID) {
	    biomeGet = 0;
	    biomePut = 4;
	} else if (biomeID == BiomeGenBase.frozenOcean.biomeID) {
	    biomeGet = 1.2;
	    biomePut = 0.5;
	} else if (biomeID == BiomeGenBase.frozenRiver.biomeID) {
	    biomeGet = 1.2;
	    biomePut = 0.5;
	} else if (biomeID == BiomeGenBase.iceMountains.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.5;
	} else if (biomeID == BiomeGenBase.icePlains.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.5;
	} else if (biomeID == BiomeGenBase.jungle.biomeID) {
	    biomeGet = 1.5;
	    biomePut = 0.5;
	} else if (biomeID == BiomeGenBase.jungleHills.biomeID) {
	    biomeGet = 1.5;
	    biomePut = 0.5;
	} else if (biomeID == BiomeGenBase.swampland.biomeID) {
	    biomeGet = 1.2;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.taiga.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.taigaHills.biomeID) {
	    biomeGet = 1;
	    biomePut = 0.75;
	} else if (biomeID == BiomeGenBase.hell.biomeID) {
	    biomeGet = 0;
	    biomePut = 4;
	}

	return new double[]{weather, biomeID, biomeGet, biomePut};
    }

    public static boolean isWater(World world, int x, int y, int z) {
	Block block = world.getBlock(x, y, z);
	return block == Blocks.water || block == Blocks.flowing_water;
    }

    public static boolean isLava(World world, int x, int y, int z) {
	Block block = world.getBlock(x, y, z);
	return block == Blocks.lava || block == Blocks.flowing_lava;
    }
}
