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

package net.lapismc.lapislogin.commands;

import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LapisLoginResetPassword {

    private LapisLogin plugin;

    public LapisLoginResetPassword(LapisLogin plugin) {
        this.plugin = plugin;
    }

    public void run(CommandSender sender, String[] args) {
        boolean isPermitted;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            isPermitted = p.hasPermission("lapislogin.admin");
        } else {
            isPermitted = true;
        }
        if (!isPermitted) {
            sender.sendMessage(plugin.LLConfig.getColoredMessage("Error.NoPermission"));
            return;
        }
        if (args.length == 1) {
            LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
            plugin.passwordManager.removePassword(loginPlayer.getOfflinePlayer().getUniqueId());
            if (loginPlayer.getOfflinePlayer().isOnline()) {
                loginPlayer.logoutPlayer();
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            }
            sender.sendMessage(plugin.LLConfig.getColoredMessage("PasswordReset").replace("%PLAYER%", loginPlayer.getOfflinePlayer().getName()));
        } else {
            sender.sendMessage(plugin.LLConfig.getColoredMessage("Error.Args"));
            sender.sendMessage(plugin.LLConfig.primaryColor + help());
        }
    }

    private String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("/resetpassword usage \n");
        sb.append("/resetpassword (player)");
        return sb.toString();
    }

}
