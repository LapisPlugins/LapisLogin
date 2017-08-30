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

import net.lapismc.lapislogin.util.PasswordHash;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public class LapisLoginPasswordManager {

    private LapisLogin plugin;
    private PasswordHash passwordHasher = new PasswordHash();
    private YamlConfiguration passwords;
    private File passwordsFile;

    protected LapisLoginPasswordManager(LapisLogin p) {
        plugin = p;
        loadPasswordsYaml();
    }

    private void loadPasswordsYaml() {
        passwordsFile = new File(plugin.getDataFolder() + "Passwords.yml");
        if (!passwordsFile.exists()) {
            try {
                passwordsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        passwords = YamlConfiguration.loadConfiguration(passwordsFile);
    }

    private void savePasswordsYaml(YamlConfiguration yaml) {
        try {
            passwords = yaml;
            passwords.save(passwordsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setPassword(UUID uuid, String pw) {
        String hash = getHash(pw);
        if (hash != null) {
            if (Bukkit.getServer().getOnlineMode()) {
                passwords.set(uuid.toString(), hash);
            } else {
                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                String name = op.getName();
                passwords.set(name, hash);
            }
            savePasswordsYaml(passwords);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPassword(UUID uuid, String pw) {
        String hash;
        if (Bukkit.getServer().getOnlineMode()) {
            hash = passwords.getString(uuid.toString());
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            String name = op.getName();
            hash = passwords.getString(name);
        }
        return checkHash(pw, hash);
    }

    private boolean checkHash(String pw, String hash) {
        try {
            return passwordHasher.validatePassword(pw, hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getHash(String pw) {
        try {
            return passwordHasher.createHash(pw);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

}
