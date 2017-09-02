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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

import java.util.Date;

public class LapisLoginListeners implements Listener {

    LapisLogin plugin;

    public LapisLoginListeners(LapisLogin p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //connect disconnect events

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (loginPlayer.task != null) {
            loginPlayer.task.cancel();
        }
        if (loginPlayer.isLoggedIn()) {
            if (!loginPlayer.getIP().equals(e.getPlayer().getAddress().getHostString())) {
                loginPlayer.sendMessage("You have been logged out");
                loginPlayer.sendMessage("OLD: " + loginPlayer.getIP() + "NEW: " + e.getPlayer().getAddress().getHostString());
                loginPlayer.logoutPlayer(false);
                return;
            }
            e.getPlayer().sendMessage(plugin.LLConfig.getColoredMessage("Login.NoLoginRequired"));
        } else {
            if (loginPlayer.isRegistered()) {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
                if (plugin.getConfig().getBoolean("HideInventory")) {
                    loginPlayer.getPlayer().getInventory().clear();
                }
            } else {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            }
            YamlConfiguration config = loginPlayer.getConfig();
            Date date = new Date();
            config.set("Login", date.getTime());
            loginPlayer.saveConfig(config);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        YamlConfiguration config = loginPlayer.getConfig();
        Date date = new Date();
        config.set("Logout", date.getTime());
        loginPlayer.saveConfig(config);
        loginPlayer.playerQuit();

    }

    //Deny action events

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            String cmd = e.getMessage().split(" ")[0].toLowerCase();
            if (!(cmd.equals("/login") || cmd.equals("/register"))) {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
                e.setCancelled(true);
            } else {
                e.setCancelled(false);
            }
        }
    }

    //inventory lock events

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent e) {
        if (!plugin.getConfig().getBoolean("InventoryLock"))
            return;
        if (e.getWhoClicked() instanceof Player) {
            LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getWhoClicked().getUniqueId());
            if (!loginPlayer.isLoggedIn()) {
                e.setCancelled(true);
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (!plugin.getConfig().getBoolean("InventoryLock"))
            return;
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            e.setCancelled(true);
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!plugin.getConfig().getBoolean("InventoryLock"))
            return;
        if (e.getEntity() instanceof Player) {
            LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getEntity().getUniqueId());
            if (!loginPlayer.isLoggedIn()) {
                e.setCancelled(true);
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            }
        }
    }

}
