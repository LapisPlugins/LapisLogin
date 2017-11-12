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
import net.lapismc.lapislogin.util.PlayerDataStore;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public class LapisLoginPasswordManager {

    private LapisLogin plugin;
    private PasswordHash passwordHasher = new PasswordHash();

    protected LapisLoginPasswordManager(LapisLogin p) {
        plugin = p;
    }

    private PlayerDataStore getPlayerData(UUID uuid) {
        return plugin.getLoginPlayer(uuid).getConfig();
    }

    public boolean isPasswordSet(UUID uuid) {
        return getPlayerData(uuid).getString("Password").equalsIgnoreCase("");
    }

    public boolean setPassword(UUID uuid, String pw) {
        String hash = getHash(pw);
        if (hash != null) {
            getPlayerData(uuid).set("Password", pw);
            return true;
        } else {
            return false;
        }
    }

    public void removePassword(UUID uuid) {
        getPlayerData(uuid).set("Password", "");
    }

    public boolean checkPassword(UUID uuid, String pw) {
        String hash = getPlayerData(uuid).getString("Password");
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
