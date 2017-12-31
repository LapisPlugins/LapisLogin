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
import net.lapismc.lapislogin.util.SQLiteDatabaseTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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

    public void convertPlayerData() {
        //set which data storage system we are using
        if (plugin.getConfig().getString("DataStorage").equalsIgnoreCase("YAML")) {
            plugin.currentDataType = PlayerDataStore.dataType.YAML;
        } else if (plugin.getConfig().getString("DataStorage").equalsIgnoreCase("MySQL")) {
            plugin.currentDataType = PlayerDataStore.dataType.MySQL;
            if (!(new MySQLDatabaseTool(plugin.getConfig()).isConnected())) {
                plugin.currentDataType = PlayerDataStore.dataType.YAML;
                plugin.logger.warning("An error occurred with MySQL, Please check your settings. For now we will use YAML");
            }
        } else if (plugin.getConfig().getString("DataStorage").equalsIgnoreCase("SQLite")) {
            plugin.currentDataType = PlayerDataStore.dataType.SQLite;
            new SQLiteDatabaseTool(plugin).setupDatabase();
        } else {
            //if the value entered doesnt match anything we just use YAML
            plugin.currentDataType = PlayerDataStore.dataType.YAML;
            plugin.logger.warning("The data storage type entered doesn't exist, please check spelling. For now we will use YAML");
        }
        File f = new File(plugin.getDataFolder(), "PlayerData");
        //if YAML has data but isn't the current data storage type then we will migrate it into the current storage type
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
        MySQLDatabaseTool sql = new MySQLDatabaseTool(plugin.getConfig());
        try {
            //if MySQL has data but isn't the current data storage type then we will migrate it into the current storage type
            if (plugin.currentDataType != PlayerDataStore.dataType.MySQL && sql.isConnected() && sql.getAllRows().isBeforeFirst()) {
                ResultSet rs = sql.getAllRows();
                try {
                    while (rs.next()) {
                        PlayerDataStore playerData = new PlayerDataStore(plugin, UUID.fromString(rs.getString("UUID")));
                        playerData.setupPlayer(rs.getString("Password"), rs.getLong("Login"), rs.getLong("Logout"), rs.getString("IPAddress"));
                        sql.dropRow(rs.getString("UUID").toString());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SQLiteDatabaseTool SQLite = new SQLiteDatabaseTool(plugin);
        //if SQLite has data but isn't the current data storage type then we will migrate it into the current storage type
        if (plugin.currentDataType != PlayerDataStore.dataType.SQLite && SQLite.isConnected()) {
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                PlayerDataStore playerData = new PlayerDataStore(plugin, p.getUniqueId());
                if (playerData.getString("Password") != null) {
                    playerData.setupPlayer(playerData.getString("Password"), playerData.getLong("Login"), playerData.getLong("Logout"), playerData.getString("IPAddress"));
                    SQLite.dropRow(p.getUniqueId().toString());
                }
            }
            SQLite.close();
            f = new File(plugin.getDataFolder(), "PlayerData.db");
            f.delete();
        }
        f = new File(plugin.getDataFolder(), "Passwords.yml");
        //if the passwords file still exists then we will move this data into the player data file and then remove the passwords file
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
        clearOldEntries();
    }

    private void clearOldEntries() {
        //for each player we see how long they have been offline and remove their data if they have been offline too long
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            PlayerDataStore playerData = new PlayerDataStore(plugin, p.getUniqueId());
            if (!playerData.hasData()) {
                return;
            }
            Long logout = playerData.getLong("Logout");
            //86400000 is the number of milliseconds in a day
            Long timeout = (plugin.getConfig().getInt("PlayerTimeout", 365) * 86400000) - new Date().getTime();
            //if timeout is bigger than logout, it means that they player was last online before the cut off point
            if (timeout > logout) {
                playerData.deletePlayer();
                plugin.logger.info("Player " + p.getName() + " has had their LapisLogin data wiped as they have been offline too long");
            }
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
