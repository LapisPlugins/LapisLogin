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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStore {

    LapisLogin plugin;
    UUID uuid;
    MySQLDatabaseTool MySQL;
    SQLiteDatabaseTool SQLite;
    HashMap<String, Map<Long, Object>> cache = new HashMap<>();

    public PlayerDataStore(LapisLogin p, UUID uuid) {
        plugin = p;
        this.uuid = uuid;
        setupMySQL();
        setupSQLite();
        if (plugin.currentDataType == dataType.YAML) {
            File f = new File(plugin.getDataFolder(), "PlayerData");
            if (!f.exists()) {
                f.mkdir();
            }
        }
    }

    private void setupMySQL() {
        if (plugin.currentDataType != dataType.MySQL || MySQL != null) {
            return;
        }
        if (plugin.mySQL == null) {
            MySQL = new MySQLDatabaseTool(plugin.getConfig());
            plugin.mySQL = MySQL;
        } else {
            MySQL = plugin.mySQL;
        }
    }

    private void setupSQLite() {
        if (plugin.currentDataType != dataType.SQLite || SQLite != null) {
            return;
        }
        if (plugin.SQLite == null) {
            SQLite = new SQLiteDatabaseTool(plugin);
            plugin.SQLite = SQLite;
        } else {
            SQLite = plugin.SQLite;
        }
    }

    public Object checkCache(String path) {
        if (!cache.containsKey(path)) {
            return null;
        }
        Date date = new Date();
        Long timeout = date.getTime() - 600000l;
        Map<Long, Object> map = cache.get(path);
        Long timeSet = (Long) map.keySet().toArray()[0];
        if (timeSet < timeout) {
            cache.remove(path);
            return null;
        }
        return map.values().toArray()[0];
    }

    public void set(String path, Object data) {
        setData(path, data);
    }

    public void setupPlayer(String password, Long login, Long logout, String ip) {
        addData(password, login, logout, ip);
    }

    private void addData(String password, Long login, Long logout, String ip) {
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
                yaml.set("Password", password);
                yaml.set("Login", login);
                yaml.set("Logout", logout);
                yaml.set("IPAddress", ip);
                try {
                    yaml.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MySQL:
                setupMySQL();
                MySQL.addData(uuid.toString(), password, login, logout, ip);
                break;
            case SQLite:
                setupSQLite();
                SQLite.addData(uuid.toString(), password, login, logout, ip);
                break;
        }
    }

    private void setData(String path, Object data) {
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
                yaml.set(path, data);
                Map<Long, Object> map = new HashMap<>();
                map.put(new Date().getTime(), data);
                cache.put(path, map);
                try {
                    yaml.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MySQL:
                setupMySQL();
                MySQL.setData(uuid.toString(), path, data);
                map = new HashMap<>();
                map.put(new Date().getTime(), data);
                cache.put(path, map);
                break;
            case SQLite:
                setupSQLite();
                SQLite.setData(uuid.toString(), path, data);
                map = new HashMap<>();
                map.put(new Date().getTime(), data);
                cache.put(path, map);
                break;
        }
    }

    public String getString(String path) {
        if (checkCache(path) != null) {
            return (String) checkCache(path);
        }
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
                String obj = yaml.getString(path);
                Map<Long, Object> map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
            case MySQL:
                setupMySQL();
                obj = MySQL.getString(uuid.toString(), path);
                map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
            case SQLite:
                setupSQLite();
                obj = SQLite.getString(uuid.toString(), path);
                map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
        }
        return null;
    }

    public long getLong(String path) {
        if (checkCache(path) != null) {
            return (Long) checkCache(path);
        }
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
                Long obj = yaml.getLong(path);
                Map<Long, Object> map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
            case MySQL:
                setupMySQL();
                obj = MySQL.getLong(uuid.toString(), path);
                map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
            case SQLite:
                setupSQLite();
                obj = SQLite.getLong(uuid.toString(), path);
                map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
        }
        return 0l;
    }

    public Object get(String path) {
        return getObject(path);
    }

    private Object getObject(String path) {
        if (checkCache(path) != null) {
            return checkCache(path);
        }
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
                Object obj = yaml.get(path);
                Map<Long, Object> map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
            case MySQL:
                setupMySQL();
                obj = MySQL.getObject(uuid.toString(), path);
                map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
            case SQLite:
                setupSQLite();
                obj = SQLite.getObject(uuid.toString(), path);
                map = new HashMap<>();
                map.put(new Date().getTime(), obj);
                cache.put(path, map);
                return obj;
        }
        return null;
    }

    public enum dataType {
        YAML, MySQL, SQLite
    }

}
