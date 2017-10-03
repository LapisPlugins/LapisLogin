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

package net.lapismc.lapislogin;

import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import net.lapismc.lapislogin.util.InventorySerialization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class LapisLogin extends JavaPlugin {

    public Logger logger = getLogger();
    public LapisUpdater updater;
    public LapisLoginPasswordManager passwordManager;
    public InventorySerialization invSerialization;
    public LapisLoginConfigurations LLConfig;
    private HashMap<UUID, LapisLoginPlayer> players = new HashMap<>();

    @Override
    public void onEnable() {
        updater = new LapisUpdater(this, "LapisLogin", "Dart2112", "LapisLogin", "master");
        if (updater.checkUpdate()) {
            if (getConfig().getBoolean("DownloadUpdates")) {
                updater.downloadUpdate();
            } else {
                logger.info("Update available for LapisLogin");
            }
        }
        passwordManager = new LapisLoginPasswordManager(this);
        invSerialization = new InventorySerialization();
        LLConfig = new LapisLoginConfigurations(this);
        new LapisLoginListeners(this);
        new LapisLoginCommands(this);
        Metrics metrics = new Metrics(this);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(LLConfig.primaryColor + "LapisLogin has been reloaded, Unfortunately this means you need to login again.\nUse /login (password) to login again");
        }
        logger.info("LapisLogin v." + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        for (LapisLoginPlayer p : players.values()) {
            p.saveConfig(p.config);
            removeLoginPlayer(p.getOfflinePlayer().getUniqueId());
        }
        logger.info("LapisLogin has been disabled!");
    }

    public LapisLoginPlayer getLoginPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new LapisLoginPlayer(this, uuid));
        } else {
            Date date = new Date();
            if (players.get(uuid).getConfig().getLong("Logout") + (getConfig().getInt("LogoutTimeout") * 60000) > date.getTime()) {
                LapisLoginPlayer loginPlayer = new LapisLoginPlayer(this, uuid);
                loginPlayer.forceLogin();
                players.put(uuid, loginPlayer);
            }
        }
        if (!players.get(uuid).getOfflinePlayer().isOnline()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    removeLoginPlayer(uuid);
                }
            }, 20 * 10);
        }
        return players.get(uuid);
    }

    public void removeLoginPlayer(UUID uuid) {
        players.remove(uuid);
    }
}
