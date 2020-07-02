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

public class LapisLoginRegister extends LapisCoreCommand {
    //TODO: maybe make a LapisLoginMasterCommand class to store plugin and have util methods?
    private LapisLogin plugin;

    public LapisLoginRegister(LapisLogin plugin) {
        super(plugin, "register", "Allows a player to set a password on their account", new ArrayList<>());
        this.plugin = plugin;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        // /register (password) (repeat password)
        if (!(sender instanceof Player)) {
            //TODO: send not player message
            sender.sendMessage("You must be a player");
            return;
        }
        LapisLoginPlayer player = plugin.getPlayer(((Player) sender).getUniqueId());
        //TODO: Check permission
        //Make sure the player isn't already registered
        if (player.isRegistered()) {
            //TODO: send proper message
            player.sendRawMessage("You are already registered");
            return;
        }
        if (args.length != 2) {
            //TODO: send help
            sender.sendMessage("/register password password");
            return;
        }
        String password = args[0];
        String confirmation = args[1];
        if (!password.equals(confirmation)) {
            //TODO: send mismatched password error
            sender.sendMessage("Those passwords dont match");
            return;
        }
        //At this point we have a confirmed password
        player.register(password);
    }
}
