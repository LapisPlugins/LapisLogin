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

import java.util.UUID;

public class LapisLoginPlayer {

    private UUID uuid;
    private boolean loggedIn = false;
    private boolean registered;

    public LapisLoginPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isRegistered() {
        return registered;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public UUID getUUID() {
        return uuid;
    }

}
