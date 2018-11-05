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


import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapislogin.playerdata.datastore.DataStore;
import net.lapismc.lapislogin.playerdata.datastore.H2DataStore;
import net.lapismc.lapislogin.playerdata.datastore.MySQLDataStore;
import net.lapismc.lapislogin.playerdata.datastore.YamlDataStore;
import org.bukkit.Bukkit;

public final class LapisLogin extends LapisCorePlugin {

    private static LapisLogin instance;
    private DataStore dataStore;

    public static LapisLogin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getLogger().info("LapisLogin v." + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LapisLogin has been disabled!");
    }

    public DataStore getDataStore() {
        if (dataStore == null) {
            switch (getConfig().getString("Storage", "H2")) {
                case "YAML":
                    dataStore = new YamlDataStore(this);
                    break;
                case "MySQL":
                    dataStore = new MySQLDataStore(this);
                    if (!((MySQLDataStore) dataStore).isConnected(true)) {
                        getLogger().warning(config.getMessage("Error.DatabaseConnectionError"));
                        dataStore = new YamlDataStore(this);
                    }
                    break;
                case "H2":
                    dataStore = new H2DataStore(this);
                    if (!((H2DataStore) dataStore).isConnected(true)) {
                        getLogger().warning(config.getMessage("Error.DatabaseConnectionError"));
                        dataStore = new YamlDataStore(this);
                    }
                    break;
                default:
                    getLogger().info("Invalid storage, the only values are YAML, MySQL or H2, Disabling");
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
