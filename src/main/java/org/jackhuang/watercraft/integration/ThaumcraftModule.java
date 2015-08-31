/**
 * Copyright (c) Huang Yuhui, 2014
 * 
 * "WaterCraft" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package org.jackhuang.watercraft.integration;

import net.minecraft.item.ItemStack;

import org.jackhuang.watercraft.common.block.GlobalBlocks;
import org.jackhuang.watercraft.common.block.ore.OreType;
import org.jackhuang.watercraft.common.block.turbines.TurbineType;
import org.jackhuang.watercraft.common.block.watermills.WaterType;
import org.jackhuang.watercraft.common.item.crafting.ItemMaterial;
import org.jackhuang.watercraft.common.item.crafting.MaterialForms;
import org.jackhuang.watercraft.common.item.crafting.MaterialTypes;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ThaumcraftModule extends BaseModule {

    @Override
    public void postInit() {
        for(int i = 0; i < WaterType.values().length; i++) {
            int b = 16 * (i + 1);
            ThaumcraftApi.registerObjectTag(new ItemStack(GlobalBlocks.waterMill, 1, i),
                    new AspectList().add(Aspect.WATER, b).add(Aspect.ENERGY, b).add(Aspect.METAL, b).add(Aspect.TOOL, b));
        }
        for(int i = 0; i < TurbineType.values().length; i++) {
            int b = 16 * (i + 5);
            ThaumcraftApi.registerObjectTag(new ItemStack(GlobalBlocks.turbine, 1, i),
                    new AspectList().add(Aspect.WATER, b).add(Aspect.ENERGY, b).add(Aspect.METAL, b).add(Aspect.TOOL, b));
        }
        for(int i = 1; i <= 7; i++) {
            ThaumcraftApi.registerObjectTag(new ItemStack(GlobalBlocks.machine, 1, i),
                    new AspectList().add(Aspect.WATER, 8).add(Aspect.ENERGY, 8).add(Aspect.METAL, 8).add(Aspect.TOOL, 8));
        }
        for(int i = 0; i <= OreType.values().length; i++) {
            ThaumcraftApi.registerObjectTag(new ItemStack(GlobalBlocks.ore, 1, i),
                    new AspectList().add(Aspect.METAL, 2));
        }
        for(MaterialForms f : MaterialForms.values())
            for(MaterialTypes t : MaterialTypes.values())
                ThaumcraftApi.registerObjectTag(ItemMaterial.get(t, f),
                        new AspectList().add(Aspect.METAL, 1));
    }
    
}
