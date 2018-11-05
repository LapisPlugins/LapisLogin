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
import org.bukkit.configuration.file.YamlConfiguration;
import org.h2.store.fs.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YamlDataStore extends DataStore {

    private final LapisLogin plugin;

    public YamlDataStore(LapisLogin p) {
        plugin = p;
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml");
        f.mkdir();
    }

    public void closeConnection() {
    }

    public void addData(String valueNames, String values) {
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<String> list = yaml.getStringList("list");
            list.add(values);
            yaml.set("list", list);
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String primaryKey, String primaryValue, String key, String value) {
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<String> list = yaml.getStringList("list");
            //get the attribute index
            int primaryIndex = 0;
            String[] attributes = Tables.valueOf("LoginPlayers").getValues().split(",");
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(primaryKey)) {
                    primaryIndex = i;
                }
            }
            int findingIndex = 0;
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(key)) {
                    findingIndex = i;
                }
            }
            String listValue = "";
            for (String s : list) {
                String[] array = s.split("#");
                if (array[primaryIndex].equals(primaryValue)) {
                    listValue = s;
                }
            }
            list.remove(listValue);
            StringBuilder newValue = new StringBuilder();
            String[] array = listValue.split("#");
            for (int i = 0; i < array.length; i++) {
                if (i != findingIndex) {
                    newValue.append(array[i]);
                } else {
                    newValue.append(value);
                }

                if (i != array.length - 1) {
                    newValue.append("#");
                }
            }
            list.add(newValue.toString());
            yaml.set("list", list);
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeData(String attribute, String value) {
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<String> list = yaml.getStringList("list");
            //get the attribute index
            int index = 0;
            String[] attributes = Tables.valueOf("LoginPlayers").getValues().split(",");
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(attribute)) {
                    index = i;
                }
            }
            List<String> toRemove = new ArrayList<>();
            for (String s : list) {
                String[] array = s.split("#");
                if (array[index].equals(value)) {
                    toRemove.add(s);
                }
            }
            list.removeAll(toRemove);
            yaml.set("list", list);
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropTable() {
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (f.exists()) {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                yaml.set("list", new ArrayList<String>());
                yaml.save(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wipeAllFiles() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            File f = new File(plugin.getDataFolder() + File.separator + "Yaml");
            FileUtils.deleteRecursive(f.getPath(), true);
        });
    }

    @Override
    public List<String> getEntireRows() {
        List<String> list = new ArrayList<>();
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            list = yaml.getStringList("list");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getString(String primaryKey, String value, String key) {
        Object obj = getObject(primaryKey, value, key);
        if (obj == null) return null;
        return (String) obj;
    }

    public Long getLong(String primaryKey, String value, String key) {
        Object obj = getObject(primaryKey, value, key);
        if (obj == null) return null;
        String str = (String) obj;
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getBoolean(String primaryKey, String value, String key) {
        String str = getString(primaryKey, value, key);
        if (str == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(str);
            return i != 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Object getObject(String primaryKey, String value, String key) {
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<String> list = yaml.getStringList("list");
            //get the attribute index
            int primaryIndex = 0;
            String[] attributes = Tables.valueOf("LoginPlayers").getValues().split(",");
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(primaryKey)) {
                    primaryIndex = i;
                }
            }
            int findingIndex = 0;
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(key)) {
                    findingIndex = i;
                }
            }
            for (String s : list) {
                String[] array = s.split("#");
                if (array[primaryIndex].equals(value)) {
                    return array[findingIndex];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getStringList(String primaryKey, String value, String key) {
        List<Object> objectList = getObjectList(primaryKey, value, key);
        List<String> stringList = new ArrayList<>();
        for (Object obj : objectList) {
            if (obj instanceof String) {
                stringList.add((String) obj);
            }
        }
        return stringList;
    }

    public List<Long> getLongList(String primaryKey, String value, String key) {
        List<Object> objectList = getObjectList(primaryKey, value, key);
        List<Long> longList = new ArrayList<>();
        for (Object obj : objectList) {
            if (obj instanceof Long) {
                longList.add((Long) obj);
            }
        }
        return longList;
    }

    private List<Object> getObjectList(String primaryKey, String value, String key) {
        List<Object> list = new ArrayList<>();

        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<String> stringList = yaml.getStringList("list");
            //get the attribute index
            int primaryIndex = 0;
            String[] attributes = Tables.valueOf("LoginPlayers").getValues().split(",");
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(primaryKey)) {
                    primaryIndex = i;
                }
            }
            int findingIndex = 0;
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(key)) {
                    findingIndex = i;
                }
            }
            for (String s : stringList) {
                String[] array = s.split("#");
                if (array[primaryIndex].equals(value)) {
                    list.add(array[findingIndex]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllRows(String key) {
        List<String> list = new ArrayList<>();
        File f = new File(plugin.getDataFolder() + File.separator + "Yaml"
                + File.separator + "LoginPlayers.yml");
        try {
            if (!f.getParentFile().exists())
                f.mkdir();
            if (!f.exists())
                f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<String> stringList = yaml.getStringList("list");
            //get the attribute index
            String[] attributes = Tables.valueOf("LoginPlayers").getValues().split(",");
            int findingIndex = 0;
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i].equalsIgnoreCase(key)) {
                    findingIndex = i;
                }
            }
            for (String s : stringList) {
                String[] array = s.split("#");
                list.add(array[findingIndex]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
