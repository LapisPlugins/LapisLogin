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
import java.util.Collections;

public class LapisLoginRegister extends LapisLoginCommand {

    protected LapisLoginRegister(LapisLogin core) {
        super(core, "register", "Set a password to secure your account", new ArrayList<>(Collections.singletonList("reg")));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (isNotPlayer(sender, "Error.MustBePlayer"))
            return;
        LapisLoginPlayer player = getPlayer(sender);
        if (!player.canRegister()) {
            //TODO send a message telling them that they aren't allowed to register
            return;
        }
        if (player.isRegistered()) {
            //TODO send message telling them that they are already registered
            return;
        }
        // reg pass pass
        //Check that they have the password twice
        if (args.length == 2) {
            //Check that the passwords match
            if (!args[0].equalsIgnoreCase(args[1])) {
                //TODO send message telling them that the passwords don't match
                return;
            }
            //if the password is registered successfully we will send them a message
            //if it isn't successful a message will be sent by the event that cancelled it
            if (player.register(args[0])) {
                //TODO send message that password was set
            }
        } else {
            //TODO send command help
        }
    }
}
