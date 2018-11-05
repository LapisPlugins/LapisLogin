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

public class LapisLoginAPIPlayer {

    private LapisLoginPlayer p;
    private LapisLogin plugin;
    private boolean loggedIn;
    private boolean Registered;
    private boolean registrationRequired;

    public LapisLoginAPIPlayer(LapisLogin plugin, LapisLoginPlayer p) {
        this.plugin = plugin;
        this.p = p;
    }

    /**
     * @return Returns true if the player is online
     * checks if the player is online
     */
    public boolean isOnline() {
        return p.getOfflinePlayer().isOnline();
    }

    /**
     * @return Returns true when the player is logged in, NOTE: the player might be offline and have an active session
     * checks if the player is logged in
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @return Returns true when the player has a password registered with LapisLogin
     * checks if the player has a password registered
     */
    public boolean isRegistered() {
        return Registered;
    }

    /**
     * @return Returns true if the player required to register, it will also return true if they are offline
     * checks if the player is required to register
     */
    public boolean isRegistrationRequired() {
        return registrationRequired;
    }

    /**
     * @param password The password you wish to check
     * @return Returns true if the password is correct
     */

    public boolean checkPassword(String password) {
        return p.checkPassword(password);
    }

    /**
     * Forces the player to login, this bypasses the need for a password and should therefore be used with caution!
     */
    public void loginPlayer() {
        if (p.getOfflinePlayer().isOnline() && !loggedIn) {
            this.loggedIn = loggedIn;
            p.forceLogin();
        }
    }

    /**
     * Sets the players logged in status to false, this will force the player to login again if they are online.
     */
    public void logoutPlayer() {
        if (p.getOfflinePlayer().isOnline() && Registered && loggedIn) {
            loggedIn = false;
            p.logoutPlayer(false);
        }
    }

    /**
     * Sets the players password, only works if the player isn't registered, it also informs the player of what their new password is.
     *
     * @param password The password you wish the player to use to login
     */
    public void registerPlayer(String password) {
        if (!Registered) {
            p.registerPlayer(password);
            Registered = true;
        }
    }

    /**
     * Removes the password on a players account, if the player is online they will be logged out first and informed of if they are required to register again
     */
    public void deregisterPlayer() {
        if (Registered) {
            p.logoutPlayer(true);
            if (registrationRequired) {
                p.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            } else {
                p.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationOptional"));
            }
        }
    }

}
