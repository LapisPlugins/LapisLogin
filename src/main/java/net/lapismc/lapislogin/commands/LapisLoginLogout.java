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

public class LapisLoginLogout extends LapisLoginCommand {

    public LapisLoginLogout(LapisLogin core) {
        super(core, "logout", "Logout to stop people using your account", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (isNotPlayer(sender, "Error.MustBePlayer"))
            return;
        LapisLoginPlayer player = getPlayer(sender);
        if (!player.isLoggedIn()) {
            sendMessage(sender, "Login.Required");
            return;
        }
        player.logout();
    }
}
