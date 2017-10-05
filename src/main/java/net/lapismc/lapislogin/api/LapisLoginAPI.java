/*
 * Copyright 2017 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapismc.lapislogin.api;

import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.playerdata.LapisLoginAPIPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class LapisLoginAPI {

    private static LapisLogin plugin;
    private static List<JavaPlugin> plugins;

    /**
     * Please don't use this!
     *
     * @param lapisLogin This is for internal use only, Please use {@link #LapisLoginAPI(JavaPlugin)}
     */
    public LapisLoginAPI(LapisLogin lapisLogin) {
        plugin = lapisLogin;
    }

    /**
     * use this constructor to get an instace of the class for access to the API features
     *
     * @param plugin This should be your plugins main class
     */
    public LapisLoginAPI(JavaPlugin plugin) {
        plugins.add(plugin);
    }

    /**
     * Use this to get the LapisLoginAPIPlayer to get access to player information
     *
     * @param uuid The {@link UUID} of the player you want data on
     * @return returns a {@link LapisLoginAPIPlayer} which can be used to both read and write information on a player
     */
    public LapisLoginAPIPlayer getLoginPlayerObject(UUID uuid) {
        return plugin.getLoginPlayer(uuid).getAPIPlayer();
    }

}
