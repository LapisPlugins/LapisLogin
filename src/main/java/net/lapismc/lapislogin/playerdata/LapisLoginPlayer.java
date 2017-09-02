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

package net.lapismc.lapislogin.playerdata;

import net.lapismc.lapislogin.LapisLogin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class LapisLoginPlayer {

    public YamlConfiguration config;
    public BukkitTask task;
    private LapisLogin plugin;
    private OfflinePlayer op;
    private boolean loggedIn = false;

    public LapisLoginPlayer(LapisLogin plugin, UUID uuid) {
        this.plugin = plugin;
        op = Bukkit.getOfflinePlayer(uuid);
        loggedIn = false;
        loadConfig();
    }

    public void loadConfig() {
        try {
            File file = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + op.getUniqueId() + ".yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginPlayer(String password) {
        if (plugin.passwordManager.checkPassword(op.getUniqueId(), password)) {
            sendMessage(plugin.LLConfig.getColoredMessage("Login.Success"));
            loadInventory();
            loadConfig();
            config.set("IPAddress", op.getPlayer().getAddress().getHostString());
            saveConfig(config);
            loggedIn = true;
        } else {
            sendMessage(plugin.LLConfig.getColoredMessage("Login.PasswordIncorrect"));
        }
    }

    public void forceLogin() {
        loggedIn = true;
    }

    public void logoutPlayer(boolean deregister) {
        loggedIn = false;
        saveInventory();
        if (!deregister && plugin.getConfig().getBoolean("HideInventory")) {
            getPlayer().getInventory().clear();
        }
        if (!deregister)
            sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
    }

    public void playerQuit() {
        if (loggedIn) {
            saveInventory();
        }
        if (isRegistered()) {
            task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!op.isOnline()) {
                        plugin.logger.info("Removed " + op.getName() + "'s player stuffs");
                        saveConfig(config);
                        plugin.removeLoginPlayer(op.getUniqueId());
                    }
                }
            }, plugin.getConfig().getInt("LogoutTimeout") * 20 * 60);
        } else {
            saveConfig(config);
            plugin.removeLoginPlayer(op.getUniqueId());
        }
    }

    public String getIP() {
        return config.getString("IPAddress");
    }

    public boolean isRegistered() {
        return plugin.passwordManager.isPasswordSet(op.getUniqueId());
    }

    public void registerPlayer(String password) {
        if (plugin.passwordManager.setPassword(op.getUniqueId(), password)) {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Success").replace("%PASSWORD%", password));
            loggedIn = true;
        } else {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Failed"));
        }
    }

    public void saveInventory() {
        if (op.getPlayer().getInventory() != null && op.getPlayer().getInventory().getContents().length > 0) {
            String inv = plugin.invSerialization.saveInventory(op.getPlayer().getInventory());
            if (inv != null) {
                config.set("Inventory", inv);
                saveConfig(config);
            }
        }
    }

    public void loadInventory() {
        if (!plugin.getConfig().getBoolean("HideInventory"))
            return;
        if (config.getString("Inventory") != null) {
            loadConfig();
            plugin.invSerialization.loadInventory(config.getString("Inventory"), op.getPlayer());
        }
    }

    public YamlConfiguration getConfig() {
        if (config != null) {
            return config;
        } else {
            loadConfig();
            return config;
        }
    }

    public void saveConfig(YamlConfiguration configuration) {
        try {
            File file = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + op.getUniqueId() + ".yml");
            configuration.save(file);
            config = configuration;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Player getPlayer() {
        if (op.isOnline()) {
            return op.getPlayer();
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer() {
        return op;
    }

    public void sendMessage(String message) {
        Bukkit.getPlayer(op.getUniqueId()).sendMessage(message);
    }

}
