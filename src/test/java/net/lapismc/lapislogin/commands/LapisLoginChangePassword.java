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

package net.lapismc.lapislogin.commands;

import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.api.events.ChangePasswordEvent;
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LapisLoginChangePassword {

    private LapisLogin plugin;

    public LapisLoginChangePassword(LapisLogin plugin) {
        this.plugin = plugin;
    }

    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.LLConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(((Player) sender).getUniqueId());
        if (!loginPlayer.isRegistered()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.MustBeRegistered"));
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            return;
        }
        if (args.length == 3) {
            String oldPassword = args[0];
            String newPassword0 = args[1];
            String newPassword1 = args[2];
            if (!newPassword0.equals(newPassword1)) {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.PasswordsDontMatch"));
                return;
            }
            if (!plugin.passwordManager.checkPassword(loginPlayer.getPlayer().getUniqueId(), oldPassword)) {
                String msg = plugin.LLConfig.getColoredMessage("Login.PasswordIncorrect");
                loginPlayer.sendMessage(msg.substring(0, msg.lastIndexOf("%ATTEMPTS%")));
                return;
            }
            ChangePasswordEvent event = new ChangePasswordEvent(loginPlayer, oldPassword, newPassword0);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.Cancelled") + event.getCancelReason());
                return;
            }
            if (plugin.passwordManager.setPassword(loginPlayer.getPlayer().getUniqueId(), newPassword0)) {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.Success").replace("%PASSWORD%", newPassword0));
            } else {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.Failed"));
            }
        } else {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.Args"));
            loginPlayer.sendMessage(plugin.LLConfig.primaryColor + help());
        }
    }

    private String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("/changepassword usage \n");
        sb.append("/changepassword (OldPassword) (NewPassword) (NewPassword)");
        return sb.toString();
    }

}
