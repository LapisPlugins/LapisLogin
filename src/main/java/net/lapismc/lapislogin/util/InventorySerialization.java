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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventorySerialization {

    public String saveInventory(Inventory inventory) {
        YamlConfiguration config = new YamlConfiguration();
        saveInventory(inventory, config);
        return config.saveToString();
    }

    public void saveInventory(Inventory inventory, ConfigurationSection destination) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                destination.set(Integer.toString(i), item);
            }
        }
    }

    public ItemStack[] loadInventory(String data) throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(data);
        return loadInventory(config);
    }

    public ItemStack[] loadInventory(ConfigurationSection source) throws InvalidConfigurationException {
        List<ItemStack> stacks = new ArrayList<>();

        try {
            for (String key : source.getKeys(false)) {
                int number = Integer.parseInt(key);
                while (stacks.size() <= number) {
                    stacks.add(null);
                }

                stacks.set(number, (ItemStack) source.get(key));
            }
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Expected a number.", e);
        }
        return stacks.toArray(new ItemStack[0]);
    }
}
