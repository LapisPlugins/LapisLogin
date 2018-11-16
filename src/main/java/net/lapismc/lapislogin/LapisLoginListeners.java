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

package net.lapismc.lapislogin;

import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import net.lapismc.lapislogin.util.LapisLoginConsoleFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class LapisLoginListeners implements Listener {

    private LapisLogin plugin;

    LapisLoginListeners(LapisLogin plugin) {
        this.plugin = plugin;
        new LapisLoginConsoleFilter();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        LapisLoginPlayer player = plugin.getLoginPlayer(e.getPlayer());
        if (player.getRegisterPermission().equals(LapisLoginPlayer.registerPermission.disallowed))
            return;
        if (!player.canInteract()) {
            player.startRepeatingMessages();
        }
        if (player.getRegisterPermission().equals(LapisLoginPlayer.registerPermission.optional)) {
            player.sendMessage("Register.CanRegister");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        processPlayerEvent(e, e.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        processPlayerEvent(e, e.getPlayer());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!(e.getMessage().startsWith("/login") || e.getMessage().startsWith("/register"))) {
            processPlayerEvent(e, e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            processPlayerEvent(e, (Player) e.getEntity());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            processPlayerEvent(e, (Player) e.getWhoClicked());
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        processPlayerEvent(e, e.getPlayer());
    }

    private void processPlayerEvent(Event e, Player p) {
        LapisLoginPlayer player = plugin.getLoginPlayer(p);
        if (!player.canInteract()) {
            if (e instanceof Cancellable) {
                ((Cancellable) e).setCancelled(true);
            }
        }
    }

}
