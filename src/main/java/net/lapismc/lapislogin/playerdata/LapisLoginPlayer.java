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
import net.lapismc.lapislogin.api.RegisterEvent;
import net.lapismc.lapislogin.playerdata.datastore.Passwords;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class LapisLoginPlayer {

    private LapisLogin plugin;
    private UUID uuid;
    private boolean loggedIn;

    public LapisLoginPlayer(LapisLogin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.loggedIn = false;
    }

    public LapisLoginPlayer(LapisLogin plugin, OfflinePlayer op) {
        this(plugin, op.getUniqueId());
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean login(String password) {
        loggedIn = isRegistered() && new PasswordManager(plugin).checkPassword(password, uuid);
        return loggedIn;
    }

    public boolean isRegistered() {
        String password = plugin.getDataStore().getString(new Passwords(), "UUID", uuid.toString(), "Password");
        return password != null;
    }

    public boolean canRegister() {
        //TODO this will be permission based
        return false;
    }

    public boolean register(String password) {
        RegisterEvent event = new RegisterEvent(this, password);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            Bukkit.getPlayer(uuid).sendMessage(event.getReason());
            return false;
        }
        new PasswordManager(plugin).setPassword(password, uuid);
        return true;
    }

    public boolean checkPassword(String password) {
        return new PasswordManager(plugin).checkPassword(password, uuid);
    }

}
