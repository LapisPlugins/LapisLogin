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

package net.lapismc.lapislogin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LapisLoginConfigurations {

    LapisLogin plugin;
    YamlConfiguration messages;
    File messagesFile;
    public String primaryColor = ChatColor.AQUA.toString();
    public String secondaryColor = ChatColor.RED.toString();

    public LapisLoginConfigurations(LapisLogin p) {
        plugin = p;
        p.saveDefaultConfig();
        new File(p.getDataFolder(), "PlayerData").mkdirs();
        primaryColor = ChatColor.translateAlternateColorCodes('&', getMessages().getString("PrimaryColor"));
        secondaryColor = ChatColor.translateAlternateColorCodes('&', getMessages().getString("SecondaryColor"));
    }

    private YamlConfiguration getMessages() {
        if (messages == null) {
            if (messagesFile == null) {
                messagesFile = new File(plugin.getDataFolder() + File.separator + "Messages.yml");
                if (!messagesFile.exists()) {
                    plugin.saveResource("Messages.yml", false);
                }
            }
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        }
        return messages;
    }

    public String getColoredMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', getMessages().getString(path).replace("&p", primaryColor).replace("&s", secondaryColor));
    }

    public String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
    }

}
