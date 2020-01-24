/*
 * Copyright 2020 Benjamin Martin
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

import org.bukkit.configuration.file.YamlConfiguration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class LapisLoginPasswordManager {

    private LapisLogin plugin;
    private File passwordsFile;
    private YamlConfiguration passwordsYml;
    private HashMap<UUID, String> passwordsStore = new HashMap<>();

    protected LapisLoginPasswordManager(LapisLogin plugin) {
        this.plugin = plugin;
        passwordsFile = new File(plugin.getDataFolder(), "passwords.yml");
        passwordsYml = YamlConfiguration.loadConfiguration(passwordsFile);
        loadPasswords();
    }

    /**
     * Hash and set a players password
     *
     * @param uuid     The UUID of the player
     * @param password The plain text password to be stored
     */
    public void setPassword(UUID uuid, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        passwordsStore.put(uuid, hashedPassword);
        savePasswords();
    }

    /**
     * Check if a players plain text password matches the stored password
     *
     * @param uuid     The UUID of the player
     * @param password The plain text password to test
     * @return True if the password is correct, false if the password is incorrect or one isn't stored
     */
    public boolean checkPassword(UUID uuid, String password) {
        if (!passwordsStore.containsKey(uuid))
            return false;
        String hashedPassword = passwordsStore.get(uuid);
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Save all passwords to file, this should be done when the server stops
     * and when a password changes as a failsafe for server crashes
     */
    protected void savePasswords() {
        for (UUID uuid : passwordsStore.keySet()) {
            passwordsYml.set("Passwords." + uuid.toString(), passwordsStore.get(uuid));
        }
        try {
            passwordsYml.save(passwordsFile);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Unable to save passwords to file!");
        }
    }

    private void loadPasswords() {
        for (String key : passwordsYml.getConfigurationSection("Passwords").getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String password = passwordsYml.getString("Passwords." + key);
            passwordsStore.put(uuid, password);
        }
    }

}
