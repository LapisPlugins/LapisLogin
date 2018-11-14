/*
 * Copyright 2018 Benjamin Martin
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
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Use this class to get the LapisLoginPlayer class
 */
public class LapisLoginPlayerAPI {

    private static LapisLogin plugin;

    public LapisLoginPlayerAPI(LapisLogin plugin) {
        LapisLoginPlayerAPI.plugin = plugin;
    }

    public LapisLoginPlayerAPI() {
    }

    public LapisLoginPlayer getPlayer(UUID uuid) {
        return plugin.getLoginPlayer(uuid);
    }

    public LapisLoginPlayer getPlayer(OfflinePlayer op) {
        return plugin.getLoginPlayer(op);
    }

}
