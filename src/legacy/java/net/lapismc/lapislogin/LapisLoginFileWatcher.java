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

package net.lapismc.lapislogin;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class LapisLoginFileWatcher {
    private LapisLogin plugin;

    LapisLoginFileWatcher(LapisLogin p) {
        plugin = p;
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                watcher();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void watcher() throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(plugin.getDataFolder().getAbsolutePath());
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        plugin.logger.info("LapisLogin's file watcher started!");
        WatchKey key = watcher.take();
        while (key != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                File f = fileName.toFile();
                if (kind == ENTRY_DELETE) {
                    if (f.getName().endsWith(".yml")) {
                        String name = f.getName().replace(".yml", "");
                        switch (name) {
                            case "config":
                                plugin.saveDefaultConfig();
                                break;
                            case "Messages":
                                plugin.LLConfig.getMessages(true);
                                break;
                        }
                    }
                } else if (kind == ENTRY_MODIFY) {
                    if (f.getName().endsWith(".yml")) {
                        checkConfig(f);
                    }
                }
            }
            key.reset();
            key = watcher.take();
        }
        plugin.logger.severe("LapisLogin's file watcher has stopped, please report any errors to dart2112 if this was not intended");
    }

    private void checkConfig(File f) {
        String name = f.getName().replace(".yml", "");
        switch (name) {
            case "config":
                plugin.reloadConfig();
                plugin.LLConfig.convertPlayerData();
                plugin.logger.info("Changes made to LapisLogin's config have been loaded");
                break;
            case "Messages":
                plugin.LLConfig.getMessages(true);
                plugin.logger.info("Changes made to LapisLogin's Messages.yml have been loaded");
                break;
        }
    }
}
