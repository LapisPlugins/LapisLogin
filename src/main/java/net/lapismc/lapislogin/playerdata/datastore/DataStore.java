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

package net.lapismc.lapislogin.playerdata.datastore;

import net.lapismc.lapislogin.LapisLogin;
import org.bukkit.Bukkit;

import java.util.List;

public abstract class DataStore {

    public abstract void closeConnection();

    public abstract void addData(String valueNames, String values);

    public abstract void setData(String primaryKey, String primaryValue, String key, String value);

    public abstract Long getLong(String primaryKey, String value, String key);

    public abstract String getString(String primaryKey, String value, String key);

    public abstract Boolean getBoolean(String primaryKey, String value, String key);

    public abstract Object getObject(String primaryKey, String value, String key);

    public abstract List<Long> getLongList(String primaryKey, String value, String key);

    public abstract List<String> getStringList(String primaryKey, String value, String key);

    public abstract List<String> getAllRows(String key);

    public abstract void removeData(String attribute, String value);

    protected abstract void dropTable();

    protected abstract List<String> getEntireRows();

    public void convertData(DataStore to, LapisLogin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Tables t : Tables.values()) {
                List<String> allRows = getEntireRows();
                plugin.getLogger().info(t.getName() + ": " + allRows.toString());
                for (String values : allRows) {
                    to.addData(t.getValues(), values);
                }
                dropTable();
            }
            plugin.resetDataStore();
        });
    }
}
