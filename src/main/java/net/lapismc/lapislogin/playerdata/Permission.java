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

import net.lapismc.lapiscore.LapisPermission;

public enum Permission {

    Required(new Required()), Admin(new Admin());

    private final LapisPermission permission;

    Permission(LapisPermission permission) {
        this.permission = permission;
    }

    public LapisPermission getPermission() {
        return this.permission;
    }

    private static class Required extends LapisPermission {
        //Players with this permission will be forced to register
        //0 = optional, 1 = required, 2 = disallowed
        Required() {
            super("Required");
        }
    }

    private static class Admin extends LapisPermission {
        //Players with this permission will be forced to register
        //Without this permission it will be optional, unless they have ignore
        Admin() {
            super("Admin");
        }
    }

}
