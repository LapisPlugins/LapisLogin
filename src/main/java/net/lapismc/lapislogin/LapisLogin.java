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


import net.lapismc.datastore.DataStore;
import net.lapismc.datastore.util.LapisURL;
import net.lapismc.datastore.util.URLBuilder;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapislogin.playerdata.LapisLoginPlayer;
import net.lapismc.lapislogin.playerdata.datastore.H2DataStore;
import net.lapismc.lapislogin.playerdata.datastore.MySQLDataStore;
import net.lapismc.lapislogin.playerdata.datastore.SQLiteDataStore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class LapisLogin extends LapisCorePlugin {

    private DataStore dataStore;
    private HashMap<UUID, LapisLoginPlayer> players = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("LapisLogin v." + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LapisLogin has been disabled!");
    }

    public LapisLoginPlayer getLoginPlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            players.put(uuid, new LapisLoginPlayer(this, uuid));
        }
        return players.get(uuid);
    }

    public LapisLoginPlayer getLoginPlayer(OfflinePlayer op) {
        return getLoginPlayer(op.getUniqueId());
    }

    public DataStore getDataStore() {
        if (dataStore == null) {
            LapisURL url;
            switch (getConfig().getString("DataStore.Type", "H2")) {
                case "MySQL":
                    String location = getConfig().getString("DataStore.MySQL.Location");
                    Integer port = getConfig().getInt("DataStore.MySQL.Port");
                    String username = getConfig().getString("DataStore.MySQL.Username");
                    String password = getConfig().getString("DataStore.MySQL.Password");
                    String database = getConfig().getString("DataStore.MySQL.Database");
                    url = new URLBuilder().setLocation(location).setPort(port).setDatabase(database).setUseSSL(true).build();
                    dataStore = new MySQLDataStore(this, url, username, password);
                    break;
                case "H2":
                    url = new URLBuilder().setLocation(new File(getDataFolder(), "PlayerData").getAbsolutePath()).build();
                    dataStore = new H2DataStore(this, url);
                    break;
                case "SQLite":
                    url = new URLBuilder().setLocation(new File(getDataFolder(), "PlayerData").getAbsolutePath()).build();
                    dataStore = new SQLiteDataStore(this, url);
                    break;
                default:
                    getLogger().info("Invalid storage, the only values are H2, MySQL or SQLite, Disabling");
                    Bukkit.getPluginManager().disablePlugin(this);
                    break;
            }
        }
        return dataStore;
    }

    public void resetDataStore() {
        getDataStore().closeConnection();
        dataStore = null;
    }

}
