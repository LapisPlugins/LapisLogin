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

package net.lapismc.lapislogin.api.events;

import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoginEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private LapisLoginPlayer loginPlayer;
    private String password;
    private String cancelReason;
    private boolean cancelled;

    public LoginEvent(LapisLoginPlayer p, String password) {
        loginPlayer = p;
        this.password = password;
    }

    //required event methods

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Please use {@link #setCancelled(boolean, String)} instead of this
     */
    @Deprecated
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    //My methods

    /**
     * @param cancel set to true to stop the player from logging in
     * @param reason this string will be appended to the login cancelled string from the messages.yml and sent to the player
     */
    public void setCancelled(boolean cancel, String reason) {
        cancelled = cancel;
        cancelReason = reason;
    }

    /**
     * @return Returns the string given as the cancelled reason, returns null if the event isn't cancelled
     */
    public String getCancelReason() {
        if (cancelled) {
            return cancelReason;
        } else {
            return null;
        }
    }

    /**
     * @return Returns true if the password being used is correct
     */
    public boolean correctPassword() {
        return loginPlayer.checkPassword(password);
    }

    /**
     * @return Returns the password the player is attempting to login with
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return Returns the {@link LapisLoginPlayer} object of the player attempting to login
     */
    public LapisLoginPlayer getLoginPlayer() {
        return loginPlayer;
    }

    /**
     * @return Returns the {@link Player} object of the player attempting to login
     */
    public Player getPlayer() {
        return loginPlayer.getPlayer();
    }

}
