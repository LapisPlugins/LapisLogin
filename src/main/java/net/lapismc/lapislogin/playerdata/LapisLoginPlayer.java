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

import net.lapismc.lapiscore.LapisPermission;
import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.api.LoginEvent;
import net.lapismc.lapislogin.api.RegisterEvent;
import net.lapismc.lapislogin.playerdata.datastore.Passwords;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (!loggedIn) {
            sendMessage("Login.IncorrectPassword");
        }
        //Call login event
        LoginEvent event = new LoginEvent(this, password);
        Bukkit.getPluginManager().callEvent(event);
        //If it gets cancelled we need to set the player as not logged in and send them the reason
        if (event.isCancelled()) {
            loggedIn = false;
            sendPlainMessage(event.getReason());
        }
        return loggedIn;
    }

    public void logout() {
        loggedIn = false;
        startRepeatingMessages();
    }

    public boolean canInteract() {
        //return false if the player needs to register or login
        if (isRegistered() && !isLoggedIn()) {
            return false;
        }
        return !getRegisterPermission().equals(registerPermission.required) || isRegistered();
    }

    public void startRepeatingMessages() {
        String key = isRegistered() ? "Register.MustRegister" : "Login.Required";
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!canInteract())
                    sendMessage(key);
                else
                    cancel();
            }
        };
        runnable.runTaskTimer(plugin, 0, 5 * 20);
    }

    public boolean isRegistered() {
        //Check if the password is set by seeing if it returns null from the database
        String password = plugin.getDataStore().getString(new Passwords(), "UUID", uuid.toString(), "Password");
        return password != null;
    }

    public boolean canRegister() {
        return !getRegisterPermission().equals(registerPermission.disallowed);
    }

    public registerPermission getRegisterPermission() {
        int required = plugin.perms.getPermissionValue(uuid, Permission.Required.getPermission());
        if (required == 1) {
            return registerPermission.required;
        } else if (required == 2) {
            return registerPermission.disallowed;
        }
        return registerPermission.optional;
    }

    public boolean register(String password) {
        //Call the register event
        RegisterEvent event = new RegisterEvent(this, password);
        Bukkit.getPluginManager().callEvent(event);
        //If the registration is cancelled then we send the player the reason and return false
        if (event.isCancelled()) {
            sendPlainMessage(event.getReason());
            return false;
        }
        new PasswordManager(plugin).setPassword(password, uuid);
        return true;
    }

    public boolean checkPassword(String password) {
        return new PasswordManager(plugin).checkPassword(password, uuid);
    }

    private void sendPlainMessage(String message) {
        Bukkit.getPlayer(uuid).sendMessage(message);
    }

    public void sendMessage(String key) {
        sendPlainMessage(plugin.config.getMessage(key));
    }

    public boolean isPermitted(LapisPermission perm) {
        return plugin.perms.isPermitted(uuid, perm);
    }

    public enum registerPermission {
        optional, required, disallowed
    }
}
