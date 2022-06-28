/*
 * Mohist - MohistMC
 * Copyright (C) 2018-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.mohistmc;

import com.mohistmc.config.MohistConfigUtil;
import com.mohistmc.libraries.CustomLibraries;
import com.mohistmc.libraries.DefaultLibraries;
import com.mohistmc.network.download.UpdateUtils;
import com.mohistmc.util.AutoDeleteMods;
import com.mohistmc.util.AutoDeletePlugins;
import com.mohistmc.util.InstallUtils;
import com.mohistmc.util.JarLoader;
import com.mohistmc.util.PluginsModsDelete;
import com.mohistmc.util.i18n.i18n;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


import static com.mohistmc.util.EulaUtil.hasAcceptedEULA;
import static com.mohistmc.util.EulaUtil.writeInfos;
import static com.mohistmc.util.InstallUtils.startInstallation;
import static com.mohistmc.util.InstallUtils.universalJar;
import static com.mohistmc.util.PluginsModsDelete.checkPlugins;

public class MohistMCStart {

    public static ArrayList<String> mainArgs = null;
    public static float javaVersion = Float.parseFloat(System.getProperty("java.class.version"));

    public static String getVersion() {
        return (MohistMCStart.class.getPackage().getImplementationVersion() != null) ? MohistMCStart.class.getPackage().getImplementationVersion() : "unknown";
    }

    public static void main(String[] args) throws Exception {
        mainArgs = new ArrayList<>(Arrays.asList(args));
        MohistConfigUtil.copyMohistConfig();
        if (MohistConfigUtil.bMohist("show_logo", "true"))
            System.out.println("\n" + "\n" +
                    " __    __   ______   __  __   __   ______   ______  \n" +
                    "/\\ \"-./  \\ /\\  __ \\ /\\ \\_\\ \\ /\\ \\ /\\  ___\\ /\\__  _\\ \n" +
                    "\\ \\ \\-./\\ \\\\ \\ \\/\\ \\\\ \\  __ \\\\ \\ \\\\ \\___  \\\\/_/\\ \\/ \n" +
                    " \\ \\_\\ \\ \\_\\\\ \\_____\\\\ \\_\\ \\_\\\\ \\_\\\\/\\_____\\  \\ \\_\\ \n" +
                    "  \\/_/  \\/_/ \\/_____/ \\/_/\\/_/ \\/_/ \\/_____/   \\/_/ \n" +
                    "                                                    \n" + "\n" +
                    "                                      " + i18n.get("mohist.launch.welcomemessage") + " - " + getVersion() + ", Java " + javaVersion);

        //Alert the user that Java 11 is still recommended to use to have a better compatibility.
        if (javaVersion < 61.0) {
            System.out.println(i18n.get("oldjava.use"));
        }

        if (MohistConfigUtil.bMohist("check_libraries", "true")) {
            DefaultLibraries.run();
            startInstallation();
        }
        CustomLibraries.loadCustomLibs();

        //The server can be run with Java 16+
        Class.forName("com.mohistmc.util.MohistModuleManager", false, URLClassLoader.newInstance(new java.net.URL[]{universalJar.toURI().toURL()})).getDeclaredConstructor().newInstance();

        // make sure gson use this EnumTypeAdapter
        Class.forName("com.google.gson.internal.bind.TypeAdapters$EnumTypeAdapter").getClassLoader();
        // Used to avoid mods using BusBuilder.builder().build() themselves
        Class.forName("net.minecraftforge.eventbus.api.BusBuilder").getClassLoader();

        if (mainArgs.contains("-noserver"))
            System.exit(0); //-noserver -> Do not run the Minecraft server, only let the installation running.

        if (!hasAcceptedEULA()) {
            System.out.println(i18n.get("eula"));
            while (!"true".equals(new Scanner(System.in).next())) ;
            writeInfos();
        }

        if (!MohistConfigUtil.bMohist("disable_plugins_blacklist", "false"))
            checkPlugins(AutoDeletePlugins.LIST, PluginsModsDelete.PLUGIN);

        if (!MohistConfigUtil.bMohist("disable_mods_blacklist", "false"))
            checkPlugins(AutoDeleteMods.LIST, PluginsModsDelete.MOD);
    }
}