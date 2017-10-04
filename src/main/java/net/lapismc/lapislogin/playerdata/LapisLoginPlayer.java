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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class LapisLoginPlayer {

    public YamlConfiguration config;
    public BukkitTask task;
    private LapisLogin plugin;
    private OfflinePlayer op;
    private ItemStack[] inv;
    private int loginAttempts = 0;
    private boolean loggedIn = false;
    public boolean registrationRequired = true;

    public LapisLoginPlayer(LapisLogin plugin, UUID uuid) {
        this.plugin = plugin;
        op = Bukkit.getOfflinePlayer(uuid);
        registrationRequired = getPlayer().hasPermission("lapislogin.required");
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

    public void playerJoin() {
        if (!isRegistered() && !registrationRequired) {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationOptional"));
            return;
        }
        if (plugin.getConfig().getBoolean("HideInventory")) {
            saveInventory();
        }
        if (task != null) {
            task.cancel();
        }
        if (isLoggedIn()) {
            if (!getIP().equals(getPlayer().getAddress().getHostString())) {
                sendMessage(plugin.LLConfig.getColoredMessage("Error.IPChangeLogout"));
                logoutPlayer(false);
            } else {
                loadInventory();
                getPlayer().sendMessage(plugin.LLConfig.getColoredMessage("Login.NoLoginRequired"));
            }
        } else {
            if (isRegistered()) {
                sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
            } else {
                sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            }
            YamlConfiguration config = getConfig();
            Date date = new Date();
            config.set("Login", date.getTime());
            saveConfig(config);
        }
    }

    public void loginPlayer(String password) {
        if (plugin.passwordManager.checkPassword(op.getUniqueId(), password)) {
            sendMessage(plugin.LLConfig.getColoredMessage("Login.Success"));
            loginAttempts = 0;
            loadInventory();
            loadConfig();
            config.set("IPAddress", op.getPlayer().getAddress().getHostString());
            saveConfig(config);
            loggedIn = true;
        } else {
            loginAttempts++;
            if (plugin.getConfig().getInt("LoginAttempts") <= loginAttempts) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("LoginAttemptsReachedCommand")
                        .replace("%NAME%", op.getName()).replace("%ATTEMPTS%", loginAttempts + ""));
            } else {
                sendMessage(plugin.LLConfig.getColoredMessage("Login.PasswordIncorrect").replace("%ATTEMPTS%", plugin.getConfig().getInt("LoginAttempts") - loginAttempts + ""));
            }
        }
    }

    public void forceLogin() {
        loggedIn = true;
    }

    public void logoutPlayer(boolean deregister) {
        loggedIn = false;
        if (plugin.getConfig().getBoolean("HideInventory")) {
            saveInventory();
        }
        if (!deregister) sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
    }

    public void playerQuit() {
        Date date = new Date();
        loadInventory();
        config.set("Logout", date.getTime());
        saveConfig(config);
        if (isRegistered() && isLoggedIn()) {
            task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!op.isOnline()) {
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

    public boolean canInteract() {
        if (isRegistered() && isLoggedIn()) {
            return true;
        }
        if (!isRegistered() && registrationRequired) {
            return false;
        }
        return false;
    }

    public void registerPlayer(String password) {
        if (plugin.passwordManager.setPassword(op.getUniqueId(), password)) {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Success").replace("%PASSWORD%", password));
            loadInventory();
            loggedIn = true;
        } else {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Failed"));
        }
    }

    public void saveInventory() {
        inv = getPlayer().getInventory().getContents();
        getPlayer().getInventory().clear();
    }

    public void loadInventory() {
        if (!plugin.getConfig().getBoolean("HideInventory")) return;
        if (inv != null) {
            op.getPlayer().getInventory().setContents(inv);
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
