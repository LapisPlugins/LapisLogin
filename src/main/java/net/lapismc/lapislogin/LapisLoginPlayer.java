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

import java.util.UUID;

/**
 * Represents a player on the server
 * <p>
 * Stores the login and registered state of the player as well as providing methods to query and manipulate these states
 */
public class LapisLoginPlayer {

    private UUID uuid;
    private boolean loggedIn = false;
    private boolean registered;

    /**
     * Initialize for a player
     *
     * @param uuid The UUID of the player
     */
    public LapisLoginPlayer(UUID uuid) {
        this.uuid = uuid;
        registered = LapisLogin.getInstance().passwordManager.hasPasswordSet(uuid);
    }

    /**
     * Check if a player is registered
     *
     * @return True if the player has a password on record, otherwise false
     */
    public boolean isRegistered() {
        return registered;
    }

    /**
     * Check if a player is currently logged in
     *
     * @return True if the player is logged in, otherwise false
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Register a password to this player
     *
     * @param password The plain text password for the player
     */
    public void register(String password) {
        LapisLogin.getInstance().passwordManager.setPassword(uuid, password);
        registered = true;
    }

    /**
     * Attempt to login the player
     *
     * @param password The password to login the player
     * @return True if the password was correct and the player was logged in, otherwise false
     */
    public boolean login(String password) {
        if (LapisLogin.getInstance().passwordManager.checkPassword(uuid, password)) {
            loggedIn = true;
            //Unlock the player when they login successfully
            LapisLogin.getInstance().listener.unlockPlayer(uuid);
        }
        return loggedIn;
    }

    /**
     * Send the player a message directly
     *
     * @param msg The message to send
     */
    public void sendRawMessage(String msg) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Bukkit.getPlayer(uuid).sendMessage(msg);
        }
    }

    /**
     * Send the player a message from the messages.yml
     *
     * @param key The key for the message to fetch from the messages.yml
     */
    public void sendMessage(String key) {
        sendRawMessage(LapisLogin.getInstance().config.getMessage(key));
    }

    /**
     * @return The UUID of the player that this object represents
     */
    public UUID getUUID() {
        return uuid;
    }

}
