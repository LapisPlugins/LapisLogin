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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LapisLogin {

    private net.lapismc.lapislogin.LapisLogin plugin;

    public LapisLogin(net.lapismc.lapislogin.LapisLogin p) {
        plugin = p;
    }

    public void run(CommandSender sender, String[] args) {
        String primary = plugin.LLConfig.primaryColor;
        String secondary = plugin.LLConfig.secondaryColor;
        if (args.length == 0) {
            sendPluginInformation(sender);
        } else if (args.length == 1) {
            String command = args[0];
            if (command.equalsIgnoreCase("help")) {
                sendHelp(sender);
            } else if (command.equalsIgnoreCase("reload")) {
                if (!isAdmin(sender)) {
                    sender.sendMessage(plugin.LLConfig.getColoredMessage("Error.NoPermission"));
                    return;
                }
                plugin.reloadConfig();
                plugin.LLConfig.getMessages(true);
                plugin.logger.info(sender.getName() + " reloaded configs!");
                sender.sendMessage(primary + "Messages and config reloaded!");
            } else if (command.equalsIgnoreCase("update")) {
                if (!isAdmin(sender)) {
                    sender.sendMessage(plugin.LLConfig.getColoredMessage("Error.NoPermission"));
                    return;
                }
                if (plugin.updater.checkUpdate()) {
                    plugin.updater.downloadUpdate();
                    sender.sendMessage(primary + "An update has been found and will be installed on next server start!");
                } else {
                    sender.sendMessage(primary + "No update found");
                }
            }
        }
    }

    public void sendHelp(CommandSender sender) {
        String primary = plugin.LLConfig.primaryColor;
        String secondary = plugin.LLConfig.secondaryColor;
        sender.sendMessage(primary + "/lapislogin: " + secondary + "Displays plugin information");
        sender.sendMessage(primary + "/lapislogin help: " + secondary + "Displays this information");
        if (isAdmin(sender)) {
            sender.sendMessage(primary + "/lapislogin update: " + secondary + "Updates the plugin to the latest version if a newer version is available");
            sender.sendMessage(primary + "/lapislogin reload: " + secondary + "Reloads the main config and the messages config");
        }
        sender.sendMessage(primary + "/register (password) (password): " + secondary + "If both passwords match they will be set as your password allowing you to login with /login");
        sender.sendMessage(primary + "/login (password): " + secondary + "Running this command with the correct password will log you into the server");
        sender.sendMessage(primary + "/logout: " + secondary + "Using this command will logout your player, this will stop anyone using your computer from using your account");
        sender.sendMessage(primary + "/changepassword (old password) (new password) (new password): " + secondary + "If the old password is correct and both new passwords match," +
                " your password will be changed");
        if (isAdmin(sender)) {
            sender.sendMessage(primary + "/resetpassword (player): " + secondary + "This command will remove the password from a players account, this will force them to register again");
        }
    }

    public void sendPluginInformation(CommandSender sender) {
        String primary = plugin.LLConfig.primaryColor;
        String secondary = plugin.LLConfig.secondaryColor;
        String bars = secondary + "-------------";
        sender.sendMessage(bars + primary + " LapisLogin " + bars);
        sender.sendMessage(primary + "Version: " + secondary + plugin.getDescription().getVersion());
        sender.sendMessage(primary + "Author: " + secondary + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(primary + "Spigot: " + secondary + "https://goo.gl/6edyJA");
        sender.sendMessage(primary + "If you need help use " + secondary + "/lapislogin help");
        sender.sendMessage(bars + bars + bars);
    }

    private boolean isAdmin(CommandSender sender) {
        boolean admin;
        if (sender instanceof Player) {
            admin = ((Player) sender).hasPermission("lapislogin.admin");
        } else {
            admin = true;
        }
        return admin;
    }
}
