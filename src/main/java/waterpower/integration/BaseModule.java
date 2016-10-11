/**
 * Copyright (c) Huang Yuhui, 2014
 * 
 * "WaterCraft" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package waterpower.integration;

public class BaseModule {

    protected void testClassExistence(Class c) {
        c.isInstance(this);
    }

    public void init() {
    }

    public void postInit() {
    }

}