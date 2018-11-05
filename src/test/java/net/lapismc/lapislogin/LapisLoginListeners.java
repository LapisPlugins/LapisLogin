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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

import java.util.Collections;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LapisLoginListeners implements Listener {

    LapisLogin plugin;
    Filter consoleLogListener = new Filter() {
        @Override
        public boolean isLoggable(LogRecord record) {
            record.setMessage(removePasswords(record.getMessage()));
            return true;
        }
    };

    //connect disconnect events

    public LapisLoginListeners(LapisLogin p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getLogger().setFilter(consoleLogListener);
        setLog4JFilter();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        loginPlayer.playerJoin();
    }

    //Deny action events

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        loginPlayer.playerQuit();
    }

    private boolean denyAction(PlayerEvent e) {
        if (e instanceof Cancellable) {
            Cancellable c = (Cancellable) e;
            if (c.isCancelled()) {
                return true;
            }
        }
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.canInteract() && e.getPlayer().getVelocity().getY() < 0) {
            Location y = e.getPlayer().getWorld().getHighestBlockAt(e.getPlayer().getLocation()).getLocation();
            Location loc = e.getPlayer().getLocation();
            loc.setY(y.getY());
            e.getPlayer().teleport(loc);
            return false;
        }
        if (!loginPlayer.canInteract()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(denyAction(e));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(denyAction(e));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if ((loginPlayer.registrationRequired || loginPlayer.isRegistered()) && !loginPlayer.isLoggedIn()) {
            e.setCancelled(true);
        }
    }

    //inventory lock events

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().split(" ")[0].toLowerCase();
        if (!(cmd.equals("/login") || cmd.equals("/register"))) {
            e.setCancelled(denyAction(e));
        } else {
            e.setCancelled(false);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent e) {
        if (!plugin.getConfig().getBoolean("InventoryLock"))
            return;
        if (e.getWhoClicked() instanceof Player) {
            LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getWhoClicked().getUniqueId());
            if (!loginPlayer.isLoggedIn() && loginPlayer.registrationRequired) {
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
        if (!loginPlayer.isLoggedIn() && loginPlayer.registrationRequired) {
            e.setCancelled(true);
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
        }
    }

    //console log event for hiding passwords

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if (!plugin.getConfig().getBoolean("InventoryLock"))
            return;
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(e.getPlayer().getUniqueId());
        if (!loginPlayer.isLoggedIn() && loginPlayer.registrationRequired) {
            e.setCancelled(true);
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.ActionDenied"));
        }
    }

    private void setLog4JFilter() {
        AbstractFilter abstractConsoleLogListener = new AbstractFilter() {

            private Result validateMessage(Message message) {
                if (message == null) {
                    return Result.NEUTRAL;
                }
                return validateMessage(message.getFormattedMessage());
            }

            private Result validateMessage(String message) {
                Result r = removePasswords(message).equalsIgnoreCase(message) ? Result.NEUTRAL : Result.DENY;
                if (r == Result.DENY) {
                    Bukkit.getLogger().info(removePasswords(message));
                }
                return r;
            }

            @Override
            public Result filter(LogEvent event) {
                Message candidate = null;
                if (event != null) {
                    candidate = event.getMessage();
                }
                return validateMessage(candidate);
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
                return validateMessage(msg);
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
                String candidate = null;
                if (msg != null) {
                    candidate = msg.toString();
                }
                return validateMessage(candidate);
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
                return validateMessage(msg);
            }
        };
        org.apache.logging.log4j.core.Logger logger;
        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(abstractConsoleLogListener);
    }

    public String removePasswords(String msg) {
        if (msg.contains("issued server command: /login ")) {
            return (msg.substring(0, msg.lastIndexOf("/login")) + hidePasswords(msg.substring(msg.lastIndexOf("/login"))));
        } else if (msg.contains("issued server command: /register ")) {
            return (msg.substring(0, msg.lastIndexOf("/register")) + hidePasswords(msg.substring(msg.lastIndexOf("/register"))));
        } else if (msg.contains("issued server command: /changepassword ")) {
            return (msg.substring(0, msg.lastIndexOf("/changepassword")) + hidePasswords(msg.substring(msg.lastIndexOf("/changepassword"))));
        }
        return msg;
    }

    private String hidePasswords(String msg) {
        StringBuilder hidden = new StringBuilder();
        for (String s : msg.split(" ")) {
            int n = s.length();
            if (s.startsWith("/")) {
                hidden.append(s);
            } else {
                hidden.append(" ").append(String.join("", Collections.nCopies(n, "*")));
            }
        }
        return hidden.toString();
    }

}
