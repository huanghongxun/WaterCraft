/**
 * Copyright (c) Huang Yuhui, 2017
 *
 * "WaterPower" is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package waterpower.annotations

import com.google.common.collect.Lists
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.relauncher.FMLLaunchHandler
import net.minecraftforge.fml.relauncher.SideOnly
import waterpower.JavaAdapter
import java.io.File
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType
import java.net.JarURLConnection
import java.net.URLDecoder
import java.util.*

object AnnotationSystem {

    val classes: LinkedList<String> = Lists.newLinkedList<String>()
    val parsers: LinkedList<MethodHandle> = Lists.newLinkedList<MethodHandle>()
    val modPath: File by lazy {
        val offset = AnnotationSystem::class.java.name.replace('.', '/') + ".class"
        val src = AnnotationSystem::class.java.getResource("/" + offset)
        when (src.protocol) {
            "jar" -> File((src.openConnection() as JarURLConnection).jarFile.name)
            "file" -> File(src.file.replace(offset, ""))
            else -> throw IllegalStateException("Cannot identify the environment, jar or file?")
        }
    }

    fun initialize() {
        classes.addAll(ClassEngine.findClassesInURL(modPath.toURL()))

        val side = FMLLaunchHandler.side()
        val loader = Launch.classLoader

        for (name in classes) {
            try {
                val clazz = Class.forName(name, false, loader)
                if (clazz.isInterface)
                    continue
                val sideOnly = clazz.getAnnotation(SideOnly::class.java)
                if (sideOnly != null && sideOnly.value != side)
                    continue
                val parser = clazz.getAnnotation(Parser::class.java)
                if (parser != null)
                    parsers.add(ClassEngine.lookup.findStatic(clazz, "loadClass", MethodType.methodType(Void.TYPE, Class::class.java)))
            } catch(ignore: ClassNotFoundException) {
            } catch(ignore2: NoClassDefFoundError) {
            }
        }

        for (name in classes) {
            try {
                val clazz = Class.forName(name, false, loader)
                if (clazz.isInterface)
                    continue
                val sideOnly = clazz.getAnnotation(SideOnly::class.java)
                if (sideOnly != null && sideOnly.value != side)
                    continue
                for (handle in parsers)
                    JavaAdapter.invokeMethodHandle(handle, clazz)
            } catch(ignore1: ClassNotFoundException) {
            } catch(ignore2: NoClassDefFoundError) {
            }
        }
    }

}