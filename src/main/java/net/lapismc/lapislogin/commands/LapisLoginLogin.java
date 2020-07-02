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

package net.lapismc.lapislogin.commands;

import net.lapismc.lapiscore.commands.LapisCoreCommand;
import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.LapisLoginPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LapisLoginLogin extends LapisCoreCommand {

    private LapisLogin plugin;

    public LapisLoginLogin(LapisLogin plugin) {
        super(plugin, "login", "Allows a player to login and unlock their player", new ArrayList<>());
        this.plugin = plugin;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        // /login password
        if (!(sender instanceof Player)) {
            //TODO: proper message
            sender.sendMessage("Must be player");
            return;
        }
        //Is a player
        LapisLoginPlayer player = plugin.getPlayer(((Player) sender).getUniqueId());
        if (args.length != 1) {
            player.sendRawMessage("You need to enter a password");
        }
        //TODO: check permissions
        //Check if they are registered and not currently logged in
        if (!player.isRegistered()) {
            //TODO: proper message
            player.sendRawMessage("Register before login");
            return;
        }
        if (player.isLoggedIn()) {
            //TODO: proper message
            player.sendRawMessage("Already logged in");
            return;
        }
        //They are registered and not logged in, grab and check the password
        String password = args[0];
        if (player.login(password)) {
            //TODO: proper msg
            player.sendRawMessage("Logged in");
        } else {
            //TODO: proper msg
            player.sendRawMessage("Incorrect password");
        }
    }
}
