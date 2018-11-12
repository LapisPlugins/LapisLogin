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
import net.lapismc.lapislogin.util.LapisLoginCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class LapisLoginLogin extends LapisLoginCommand {

    protected LapisLoginLogin(LapisLogin core) {
        super(core, "login", "Use your password to login", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (isNotPlayer(sender, "Error.MustBePlayer"))
            return;
        LapisLoginPlayer player = getPlayer(sender);
        if (!player.isRegistered()) {
            sendMessage(sender, "Register.MustRegister");
        }
        if (args.length == 1) {
            //If the player successfully logs in send them a message
            //If the login fails or is cancelled they will be sent a message by the player class
            if (player.login(args[0])) {
                sendMessage(sender, "Login.Success");
            }
        } else {
            //TODO send command help
        }
    }
}
