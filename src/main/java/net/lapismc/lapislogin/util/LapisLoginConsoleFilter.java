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

package net.lapismc.lapislogin.util;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.util.Collections;

public class LapisLoginConsoleFilter {

    public LapisLoginConsoleFilter() {
        Bukkit.getServer().getLogger().setFilter(record -> {
            record.setMessage(removePasswords(record.getMessage()));
            return true;
        });
        setLog4JFilter();
    }

    private void setLog4JFilter() {
        AbstractFilter abstractConsoleLogListener = new AbstractFilter() {

            private Result validateMessage(Message message) {
                if (message == null) {
                    return Result.NEUTRAL;
                }
                return validateMessage(message.getFormattedMessage());
            }

            private Result validateMessage(String message) {
                Result r = removePasswords(message).equalsIgnoreCase(message) ? Result.NEUTRAL : Result.DENY;
                if (r == Result.DENY) {
                    Bukkit.getLogger().info(removePasswords(message));
                }
                return r;
            }

            @Override
            public Result filter(LogEvent event) {
                Message candidate = null;
                if (event != null) {
                    candidate = event.getMessage();
                }
                return validateMessage(candidate);
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
                return validateMessage(msg);
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
                String candidate = null;
                if (msg != null) {
                    candidate = msg.toString();
                }
                return validateMessage(candidate);
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
                return validateMessage(msg);
            }
        };
        Logger logger;
        logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(abstractConsoleLogListener);
    }

    private String removePasswords(String msg) {
        if (msg.contains("issued server command: /login ")) {
            return (msg.substring(0, msg.lastIndexOf("/login")) + hidePasswords(msg.substring(msg.lastIndexOf("/login"))));
        } else if (msg.contains("issued server command: /register ")) {
            return (msg.substring(0, msg.lastIndexOf("/register")) + hidePasswords(msg.substring(msg.lastIndexOf("/register"))));
        } else if (msg.contains("issued server command: /changepassword ")) {
            return (msg.substring(0, msg.lastIndexOf("/changepassword")) + hidePasswords(msg.substring(msg.lastIndexOf("/changepassword"))));
        }
        return msg;
    }

    private String hidePasswords(String msg) {
        StringBuilder hidden = new StringBuilder();
        for (String s : msg.split(" ")) {
            int n = s.length();
            if (s.startsWith("/")) {
                hidden.append(s);
            } else {
                hidden.append(" ").append(String.join("", Collections.nCopies(n, "*")));
            }
        }
        return hidden.toString();
    }

}
