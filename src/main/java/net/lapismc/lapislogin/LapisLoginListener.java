/*
 * Copyright 2020 Benjamin Martin
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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LapisLoginListener implements Listener {

    private LapisLogin plugin;
    private Set<UUID> loggedOutPlayers = new HashSet<>();

    protected LapisLoginListener(LapisLogin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) {
        LapisLoginPlayer player = plugin.getPlayer(e.getPlayer().getUniqueId());
        //TODO: this is where optional and forced registration will take place
        if (player.isRegistered() && !player.isLoggedIn()) {
            loggedOutPlayers.add(player.getUUID());
            //TODO: make this a better message
            player.sendRawMessage("You need to login with /login (password)");
        }
    }

    //TODO: Do the locked player stuffs

    /**
     * Unlocks a player by removing them from the list of logged out players
     *
     * @param uuid The UUID of the player
     */
    public void unlockPlayer(UUID uuid) {
        loggedOutPlayers.remove(uuid);
    }

}
