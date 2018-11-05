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

package net.lapismc.lapislogin.playerdata.datastore;

import net.lapismc.lapislogin.LapisLogin;
import org.h2.Driver;

import java.io.File;
import java.sql.*;

public class H2DataStore extends MySQLDataStore {

    private final String url;

    public H2DataStore(LapisLogin plugin, boolean checkingForConversion) {
        super(plugin, "jdbc:h2:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "LapisBans;TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0;");
        this.checkingForConversion = checkingForConversion;
        Driver.load();
        url = "jdbc:h2:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "LapisBans;TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0;";
        if (!checkingForConversion) {
            setupDatabase();
        }
    }

    public H2DataStore(LapisLogin plugin) {
        this(plugin, false);
    }

    @Override
    Connection getConnection(boolean forceNewConnection) {
        try {
            if (conn == null || conn.isClosed() || forceNewConnection) {
                conn = DriverManager.getConnection(url);
            }
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    ResultSet getAllRows() {
        boolean tableExists = true;
        try {
            if (checkingForConversion) {
                ResultSet rs = getConnection(false).getMetaData().getTables(null, null, Tables.LoginPlayers.getName().toUpperCase(), null);
                tableExists = rs.next();
            }
            if (!tableExists) {
                return getConnection(false).createStatement().executeQuery("SELECT TOP 0 0");
            }
            conn = getConnection(true);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM LoginPlayers";
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    void setupDatabase() {
        try {
            conn = getConnection(true);
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + Tables.LoginPlayers.getName() + " (" +
                    "UUID VARCHAR(36) NOT NULL," +
                    "Username VARCHAR(128)," +
                    "Permission VARCHAR(128)," +
                    "IPAddress VARCHAR(15)," +
                    "OfflineTime BIGINT," +
                    "Password VARCHAR(128))";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected(boolean printStackTrace) {
        try {
            connectionTime = System.currentTimeMillis();
            getConnection(false);
            if (checkingForConversion) {
                ResultSet rs = conn.getMetaData().getTables(null, null, Tables.LoginPlayers.getName(), null);
                return rs.next();
            } else {
                return true;
            }
        } catch (SQLException e) {
            if (printStackTrace)
                e.printStackTrace();
            return false;
        }
    }
}
