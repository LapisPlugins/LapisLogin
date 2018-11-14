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

package net.lapismc.lapislogin;

import net.lapismc.lapislogin.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LapisLoginCommands implements CommandExecutor {

    private LapisLogin plugin;
    private LapisLoginLogin login;
    private LapisLoginLogout logout;
    private LapisLoginRegister register;
    private LapisLoginResetPassword resetPassword;
    private LapisLoginChangePassword changePassword;
    private net.lapismc.lapislogin.commands.LapisLogin lapisLogin;

    public LapisLoginCommands(LapisLogin p) {
        plugin = p;
        login = new LapisLoginLogin(plugin);
        logout = new LapisLoginLogout(plugin);
        register = new LapisLoginRegister(plugin);
        resetPassword = new LapisLoginResetPassword(plugin);
        changePassword = new LapisLoginChangePassword(plugin);
        lapisLogin = new net.lapismc.lapislogin.commands.LapisLogin(plugin);
        plugin.getCommand("login").setExecutor(this);
        plugin.getCommand("logout").setExecutor(this);
        plugin.getCommand("register").setExecutor(this);
        plugin.getCommand("lapislogin").setExecutor(this);
        plugin.getCommand("resetpassword").setExecutor(this);
        plugin.getCommand("changepassword").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lapislogin")) {
            lapisLogin.run(sender, args);
            return true;
        } else if (cmd.getName().equals("login")) {
            login.run(sender, args);
            return true;
        } else if (cmd.getName().equals("logout")) {
            logout.run(sender, args);
            return true;
        } else if (cmd.getName().equals("register")) {
            register.run(sender, args);
            return true;
        } else if (cmd.getName().equals("resetpassword")) {
            resetPassword.run(sender, args);
            return true;
        } else if (cmd.getName().equals("changepassword")) {
            changePassword.run(sender, args);
            return true;
        }
        return false;
    }
}
