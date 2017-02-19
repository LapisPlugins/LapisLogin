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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class LapisLoginListeners implements Listener {

    LapisLogin plugin;
    ArrayList<UUID> loggedOut = new ArrayList<>();

    public LapisLoginListeners(LapisLogin p) {
        plugin = p;
    }

    public void loginPlayer(Player p) {
        if (loggedOut.contains(p.getUniqueId())) {
            loggedOut.remove(p.getUniqueId());
            p.sendMessage(""); //TODO: add messages
            //TODO: add inventory, teleport and possibly log
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        //TODO: add API Event Fire
        loggedOut.add(p.getUniqueId());
        p.sendMessage(plugin.LLConfig.getColoredMessage("Login.LoginRequired")); //TODO: add message
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (loggedOut.contains(p.getUniqueId())) {
            loggedOut.remove(p.getUniqueId());
        } else {
            //TODO: add timout for login
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (loggedOut.contains(p.getUniqueId())) {
            p.sendMessage(""); //TODO: add message
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (loggedOut.contains(p.getUniqueId())) {
            p.sendMessage(""); //TODO: add message
            e.setCancelled(true);
        }
    }

}
