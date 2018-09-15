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

package net.lapismc.lapislogin.api;

import net.lapismc.lapislogin.LapisLogin;
import net.lapsimc.lapisinventories.LapisInventoriesAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class LapisInventoriesHook {

    LapisInventoriesAPI api = new LapisInventoriesAPI();

    public LapisInventoriesHook(LapisLogin p) {
        api.addLoginHook(p);
    }

    public void saveInventory(Player p, GameMode gm) {
        if (p.isOnline()) {
            api.hideInventory(p, gm);
        }
    }

    public void loadInventory(Player p, GameMode gm) {
        if (p.isOnline()) {
            api.giveInventory(p, gm);
        }
    }

    public void loginComplete(Player p) {
        if (p.isOnline()) {
            api.loginComplete(p, p.getGameMode());
        }
    }

}