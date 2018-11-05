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

package net.lapismc.lapislogin.playerdata;

import net.lapismc.lapislogin.LapisLogin;
import org.mindrot.BCrypt;

import java.util.UUID;

public class PasswordManager {

    private LapisLogin plugin;

    public PasswordManager(LapisLogin plugin) {
        this.plugin = plugin;
    }

    public void setPassword(String password, UUID uuid) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        plugin.getDataStore().setData("UUID", uuid.toString(), "Password", hashed);
    }

    public boolean checkPassword(String password, UUID uuid) {
        String hashed = plugin.getDataStore().getString("UUID", uuid.toString(), "Password");
        if (hashed.equals("")) {
            return false;
        }
        return BCrypt.checkpw(password, hashed);
    }

}
