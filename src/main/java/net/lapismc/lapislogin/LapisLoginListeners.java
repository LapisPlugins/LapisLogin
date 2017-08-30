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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class LapisLoginListeners implements Listener {

    LapisLogin plugin;

    public LapisLoginListeners(LapisLogin p) {
        plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        LapisLoginPlayer loginPlayer = new LapisLoginPlayer(plugin, e.getPlayer().getUniqueId());
        loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired"));
        YamlConfiguration config = loginPlayer.getConfig();
        Date date = new Date();
        config.set("Login", date.getTime());
        loginPlayer.saveConfig(config);
        loginPlayer.getPlayer().getInventory().clear();
        plugin.players.put(loginPlayer.getPlayer().getUniqueId(), loginPlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        LapisLoginPlayer loginPlayer = plugin.players.get(e.getPlayer().getUniqueId());
        loginPlayer.saveInventory();
        YamlConfiguration config = loginPlayer.getConfig();
        Date date = new Date();
        config.set("Logout", date.getTime());
        loginPlayer.saveConfig(config);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        LapisLoginPlayer loginPlayer = plugin.players.get(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        LapisLoginPlayer loginPlayer = plugin.players.get(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            e.setCancelled(true);
        }
    }

}
