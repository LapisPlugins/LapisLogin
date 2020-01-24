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


import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;

import java.util.HashMap;
import java.util.UUID;

public class LapisLogin extends LapisCorePlugin {

    private static LapisLogin instance;
    private HashMap<UUID, LapisLoginPlayer> players = new HashMap<>();
    public LapisLoginPasswordManager passwordManager;

    public static LapisLogin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        passwordManager.savePasswords();
    }

    public LapisLoginPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new LapisLoginPlayer(uuid));
        }
        return players.get(uuid);
    }

    @Override
    public void onEnable() {
        instance = this;
        passwordManager = new LapisLoginPasswordManager(this);
        registerConfiguration(new LapisCoreConfiguration(this, 1, 1));
    }

}
