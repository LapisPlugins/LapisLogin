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

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class LapisLogin extends JavaPlugin {

    Logger logger = Bukkit.getLogger();
    LapisUpdater updater;
    LapisLoginPasswordManager passwordManager;
    LapisLoginConfigurations LLConfig;

    @Override
    public void onEnable() {
        updater = new LapisUpdater(this, "LapisLogin", "Dart2112", "LapisLogin", "master");
        if (updater.checkUpdate("LapisLogin")) {
            if (getConfig().getBoolean("DownloadUpdates")) {
                updater.downloadUpdate("LapisLogin");
            } else {
                logger.info("Update available for LapisLogin");
            }
        }
        passwordManager = new LapisLoginPasswordManager(this);
        LLConfig = new LapisLoginConfigurations(this);
        Metrics metrics = new Metrics(this);
        metrics.start();
        logger.info("LapisLogin v." + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {

        logger.info("LapisLogin has been disabled!");
    }
}
