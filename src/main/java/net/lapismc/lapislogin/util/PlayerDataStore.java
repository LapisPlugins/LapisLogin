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

package net.lapismc.lapislogin.util;

import net.lapismc.lapislogin.LapisLogin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataStore {

    LapisLogin plugin;
    UUID uuid;

    public PlayerDataStore(LapisLogin p, UUID uuid) {
        plugin = p;
        this.uuid = uuid;
    }

    private Object getData(String path) {
        switch (plugin.currentDataType) {
            case YAML:
                File f = new File(plugin.getDataFolder(), "PlayerData" + File.separator + uuid.toString() + ".yml");
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                return yaml.get(path);
            case MySQL:
                MySQLDatabaseTool sql = new MySQLDatabaseTool("localhost:3360", "username", "Password123", "Database");
                return sql.getData(uuid.toString(), path);
            case SQLite:
                //TODO: create database file and table if it doesn't exist, after that get the data
        }
        return null;
    }

    public enum dataType {
        YAML, MySQL, SQLite
    }

}
