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
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LapisLoginLogin extends LapisLoginCommand implements Listener {

    LapisLogin plugin;

    public LapisLoginLogin(LapisLogin p) {
        super("login", "logs the player in");
        plugin = p;
    }

    @EventHandler
    public void commandRegister(LapisLoginCommandRegisterEvent e) {
        e.addCommand(this);
    }

    @Override
    public void run(CommandSender sender, String[] args) {

    }

}
