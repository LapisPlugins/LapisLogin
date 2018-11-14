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
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LapisLoginLogin {

    LapisLogin plugin;

    public LapisLoginLogin(LapisLogin p) {
        plugin = p;
    }

    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.LLConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        LapisLoginPlayer loginPlayer = plugin.getLoginPlayer(((Player) sender).getUniqueId());
        if (!loginPlayer.isRegistered()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.MustBeRegistered"));
            if (loginPlayer.registrationRequired) {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationRequired"));
            } else {
                loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Register.RegistrationOptional"));
            }
            return;
        }
        if (loginPlayer.isLoggedIn()) {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Login.AlreadyLoggedIn"));
            return;
        }
        if (args.length == 1) {
            loginPlayer.loginPlayer(args[0]);
        } else {
            loginPlayer.sendMessage(plugin.LLConfig.getColoredMessage("Error.Args"));
            loginPlayer.sendMessage(plugin.LLConfig.primaryColor + help());
        }
    }

    public String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("/login usage \n");
        sb.append("/login (Password)");
        return sb.toString();
    }

}
