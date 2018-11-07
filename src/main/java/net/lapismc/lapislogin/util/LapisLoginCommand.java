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

package net.lapismc.lapislogin.util;

import net.lapismc.lapiscore.LapisCoreCommand;
import net.lapismc.lapislogin.LapisLogin;
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class LapisLoginCommand extends LapisCoreCommand {

    public LapisLogin plugin;

    protected LapisLoginCommand(LapisLogin core, String name, String desc, ArrayList<String> aliases) {
        super(core, name, desc, aliases, true);
        this.plugin = core;
    }

    public LapisLoginPlayer getPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return plugin.getLoginPlayer((OfflinePlayer) sender);
        } else {
            return null;
        }
    }

}
