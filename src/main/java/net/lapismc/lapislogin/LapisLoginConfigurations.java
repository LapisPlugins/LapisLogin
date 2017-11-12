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

import net.lapismc.lapislogin.util.MySQLDatabaseTool;
import net.lapismc.lapislogin.util.PlayerDataStore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class LapisLoginConfigurations {

    public String primaryColor = ChatColor.AQUA.toString();
    public String secondaryColor = ChatColor.DARK_AQUA.toString();
    LapisLogin plugin;
    YamlConfiguration messages;
    File messagesFile;

    public LapisLoginConfigurations(LapisLogin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        new File(plugin.getDataFolder(), "PlayerData").mkdirs();
        configVersion();
        primaryColor = ChatColor.translateAlternateColorCodes('&', getMessages(false).getString("PrimaryColor"));
        secondaryColor = ChatColor.translateAlternateColorCodes('&', getMessages(false).getString("SecondaryColor"));
    }

    private void configVersion() {
        if (plugin.getConfig().getInt("ConfigVersion") != 4) {
            File oldConfig = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config_old.yml");
            File newConfig = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
            if (!newConfig.renameTo(oldConfig)) {
                plugin.logger.info(plugin.getName() + " failed to update the config.yml");
            }
            plugin.saveDefaultConfig();

            getMessages(false);
            File oldMessages = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages_old.yml");
            if (!messagesFile.renameTo(oldMessages)) {
                plugin.logger.info(plugin.getName() + " failed to update the Messages.yml");
            }
            messages = null;
            messagesFile = null;
            getMessages(false);
            plugin.logger.info("New Configuration Generated for " + plugin.getName() + "," +
                    " Please Transfer Values From config_old.yml & Messages_old.yml");
        }
        convertPlayerData();
    }

    private void convertPlayerData() {
        if (plugin.getConfig().getString("DataStorage").equalsIgnoreCase("YAML")) {
            plugin.currentDataType = PlayerDataStore.dataType.YAML;
        } else if (plugin.getConfig().getString("DataStorage").equalsIgnoreCase("MySQL")) {
            plugin.currentDataType = PlayerDataStore.dataType.MySQL;
        } else {
            plugin.currentDataType = PlayerDataStore.dataType.YAML;
        }
        File f = new File(plugin.getDataFolder(), "PlayerData");
        if (f.exists() && !plugin.getConfig().getString("DataStorage").equalsIgnoreCase("YAML")) {
            for (File data : f.listFiles()) {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(data);
                PlayerDataStore playerData = new PlayerDataStore(plugin, UUID.fromString(data.getName().replace(".yml", "")));
                playerData.setupPlayer(yaml.getString("Password", ""), yaml.getLong("Login"), yaml.getLong("Logout"),
                        yaml.getString("IPAddress"));
                data.delete();
            }
            f.delete();
        }
        if (!f.exists() && plugin.getConfig().getString("DataStorage").equalsIgnoreCase("YAML")) {
            MySQLDatabaseTool sql = new MySQLDatabaseTool(plugin.getConfig());
            for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                PlayerDataStore playerData = new PlayerDataStore(plugin, op.getUniqueId());
                if (sql.getData(op.getUniqueId().toString(), "Password") != null)
                    playerData.setupPlayer((String) sql.getData(op.getUniqueId().toString(), "Password"), (Long) sql.getData(op.getUniqueId().toString(), "Login"),
                            (Long) sql.getData(op.getUniqueId().toString(), "Logout"), (String) sql.getData(op.getUniqueId().toString(), "IPAddress"));
            }
        }
        f = new File(plugin.getDataFolder(), "Passwords.yml");
        if (f.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            for (String s : yaml.getKeys(false)) {
                try {
                    PlayerDataStore playerData = new PlayerDataStore(plugin, UUID.fromString(s));
                    playerData.set("Password", yaml.getString(s));
                } catch (IllegalArgumentException e) {
                    PlayerDataStore playerData = new PlayerDataStore(plugin, Bukkit.getServer().getOfflinePlayer(s).getUniqueId());
                    playerData.set("Password", yaml.getString(s));
                }
                yaml.set(s, null);
            }
            f.delete();
        }
    }

    public YamlConfiguration getMessages(boolean reload) {
        if (messages == null || reload) {
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
        return ChatColor.translateAlternateColorCodes('&', getMessages(false).getString(path).replace("&p", primaryColor).replace("&s", secondaryColor));
    }

    public String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
    }

}
