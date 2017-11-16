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

import net.lapismc.lapislogin.api.LapisLoginAPI;
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import net.lapismc.lapislogin.util.InventorySerialization;
import net.lapismc.lapislogin.util.MySQLDatabaseTool;
import net.lapismc.lapislogin.util.PlayerDataStore;
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
    public PlayerDataStore.dataType currentDataType;
    public LapisLoginConfigurations LLConfig;
    public MySQLDatabaseTool mySQL;
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
        new LapisLoginFileWatcher(this);
        new LapisLoginAPI(this);
        Metrics metrics = new Metrics(this);
        for (Player p : Bukkit.getOnlinePlayers()) {
            LapisLoginPlayer loginPlayer = getLoginPlayer(p.getUniqueId());
            if (loginPlayer.isRegistered()) {
                p.sendMessage(LLConfig.getColoredMessage("Error.ReloadedPlugin"));
                p.sendMessage(LLConfig.getColoredMessage("Login.LoginRequired"));
            }
        }
        logger.info("LapisLogin v." + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        for (LapisLoginPlayer p : players.values()) {
            removeLoginPlayer(p.getOfflinePlayer().getUniqueId());
        }
        logger.info("LapisLogin has been disabled!");
    }

    public LapisLoginPlayer getLoginPlayer(UUID uuid) {
        if (!players.containsKey(uuid) || players.get(uuid) == null) {
            players.put(uuid, new LapisLoginPlayer(this, uuid));
        } else {
            Date date = new Date();
            LapisLoginPlayer loginPlayer = players.get(uuid);
            if (players.get(uuid).getConfig().getLong("Logout") + (getConfig().getLong("LogoutTimeout", 1l) * 60000l) > date.getTime() && loginPlayer.isLoggedIn()) {
                loginPlayer = new LapisLoginPlayer(this, uuid);
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
