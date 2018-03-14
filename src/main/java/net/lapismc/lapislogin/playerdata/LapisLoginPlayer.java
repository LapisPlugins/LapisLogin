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

package net.lapismc.lapislogin.playerdata;

import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.api.events.LoginEvent;
import net.lapismc.lapislogin.api.events.RegisterEvent;
import net.lapismc.lapislogin.util.PlayerDataStore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.UUID;

public class LapisLoginPlayer {

    public PlayerDataStore config;
    public BukkitTask task;
    public boolean registrationRequired = true;
    public boolean canRegister = true;
    private LapisLogin plugin;
    private OfflinePlayer op;
    private UUID uuid;
    private ItemStack[] inv;
    private LapisLoginAPIPlayer api;
    private int loginAttempts = 0;
    private boolean loggedIn = false;

    public LapisLoginPlayer(LapisLogin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        op = Bukkit.getOfflinePlayer(uuid);
        if (op.isOnline()) {
            registrationRequired = getPlayer().hasPermission("lapislogin.required");
            canRegister = getPlayer().hasPermission("lapislogin.optional") || getPlayer().hasPermission("lapislogin.required");
        }
        loggedIn = false;
        api = new LapisLoginAPIPlayer(plugin, this);
        loadConfig();
    }

    public LapisLoginAPIPlayer getAPIPlayer() {
        return api;
    }

    public void loadConfig() {
        if (config == null)
            config = new PlayerDataStore(plugin, uuid);
    }

    public void playerJoin() {
        loadConfig();
        if (config.getString("Password") == null) {
            config.setupPlayer("", 0l, 0l, "");
        }
        if (!isRegistered() && canRegister && !registrationRequired) {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationOptional"));
            return;
        }
        if (!canRegister) {
            return;
        }
        if (plugin.getConfig().getBoolean("HideInventory")) {
            saveInventory();
        }
        if (task != null) {
            task.cancel();
        }
        if (isLoggedIn()) {
            if (getIP() != null && !getIP().equals(getPlayer().getAddress().getHostString())) {
                sendMessage(plugin.LLConfig.getColoredMessage("Error.IPChangeLogout"));
                config.set("IPAddress", op.getPlayer().getAddress().getHostString());
                logoutPlayer(false);
            } else {
                loadInventory();
                getPlayer().sendMessage(plugin.LLConfig.getColoredMessage("Login.NoLoginRequired"));
            }
        } else {
            config.set("IPAddress", op.getPlayer().getAddress().getHostString());
            if (isRegistered()) {
                sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
            } else {
                sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            }
        }
    }

    public void loginPlayer(String password) {
        LoginEvent event = new LoginEvent(this, password);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            sendMessage(plugin.LLConfig.getColoredMessage("Login.Cancelled") + event.getCancelReason());
            return;
        }
        if (plugin.passwordManager.checkPassword(op.getUniqueId(), password)) {
            plugin.logger.info(plugin.LLConfig.getMessage("Login.SuccessConsole").replace("%PLAYER%", op.getName()));
            sendMessage(plugin.LLConfig.getColoredMessage("Login.Success"));
            loginAttempts = 0;
            loadInventory();
            loggedIn = true;
            Date date = new Date();
            config.set("Login", date.getTime());
        } else {
            loginAttempts++;
            if (plugin.getConfig().getInt("LoginAttempts") <= loginAttempts) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("LoginAttemptsReachedCommand")
                        .replace("%NAME%", op.getName()).replace("%ATTEMPTS%", loginAttempts + ""));
            } else {
                plugin.logger.info(plugin.LLConfig.getMessage("Login.PasswordIncorrectConsole").replace("%ATTEMPTS%", plugin.getConfig().getInt("LoginAttempts") - loginAttempts + "")
                        .replace("%PLAYER%", op.getName()));
                sendMessage(plugin.LLConfig.getColoredMessage("Login.PasswordIncorrect").replace("%ATTEMPTS%", plugin.getConfig().getInt("LoginAttempts") - loginAttempts + ""));
            }
        }
    }

    public boolean checkPassword(String password) {
        return plugin.passwordManager.checkPassword(op.getUniqueId(), password);
    }

    public void forceLogin() {
        loggedIn = true;
    }

    public void logoutPlayer(boolean deregister) {
        if (plugin.getConfig().getBoolean("HideInventory") && loggedIn) {
            saveInventory();
        }
        loggedIn = false;
        if (deregister) {
            plugin.passwordManager.removePassword(op.getUniqueId());
        } else {
            sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
        }
    }

    public void playerQuit() {
        Date date = new Date();
        loadInventory();
        config.set("Logout", date.getTime());
        if (isRegistered() && isLoggedIn()) {
            task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!op.isOnline()) {
                        plugin.removeLoginPlayer(op.getUniqueId());
                    }
                }
            }, plugin.getConfig().getInt("LogoutTimeout") * 20 * 60);
        } else {
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
        if (!canRegister || isLoggedIn()) {
            return true;
        }
        if (!isRegistered() && registrationRequired) {
            return false;
        }
        if (isRegistered() && !isLoggedIn()) {
            return false;
        }
        return true;
    }

    public void registerPlayer(String password) {
        if (isRegistered()) {
            sendMessage(plugin.LLConfig.getColoredMessage("Login.AlreadyLoggedIn"));
            return;
        }
        RegisterEvent event = new RegisterEvent(this, password);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Cancelled") + event.getCancelReason());
            return;
        }
        if (plugin.passwordManager.setPassword(op.getUniqueId(), password)) {
            plugin.logger.info(plugin.LLConfig.getMessage("Register.SuccessConsole").replace("%PLAYER%", op.getName()));
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Success").replace("%PASSWORD%", password));
            loadInventory();
            loggedIn = true;
        } else {
            sendMessage(plugin.LLConfig.getColoredMessage("Register.Failed"));
        }
    }

    public void saveInventory() {
        if (op.isOnline()) {
            if (!plugin.getConfig().getString("ForceGamemode").equalsIgnoreCase("None")) {
                switch (plugin.getConfig().getString("ForceGamemode")) {
                    case "Survival":
                        getPlayer().setGameMode(GameMode.SURVIVAL);
                        break;
                    case "Creative":
                        getPlayer().setGameMode(GameMode.CREATIVE);
                        break;
                    case "Adventure":
                        getPlayer().setGameMode(GameMode.ADVENTURE);
                        break;
                }
            }
            inv = getPlayer().getInventory().getContents();
            getPlayer().getInventory().clear();
        }
    }

    public void loadInventory() {
        if (!plugin.getConfig().getBoolean("HideInventory")) return;
        if (inv != null) {
            op.getPlayer().getInventory().setContents(inv);
        }
    }

    public PlayerDataStore getConfig() {
        loadConfig();
        return config;
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
        if (op == null) {
            op = Bukkit.getOfflinePlayer(uuid);
        }
        return op;
    }

    public void sendMessage(String message) {
        if (op.isOnline()) {
            Bukkit.getPlayer(op.getUniqueId()).sendMessage(message);
        }
    }

}
