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

package net.lapismc.lapislogin.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class MySQLDatabaseTool {

    String url = "jdbc:mysql://%URL%/%DBName%?verifyServerCertificate=false&useSSL=true";
    String username;
    String password;
    String DBName;
    FileConfiguration config;
    Connection conn;

    public MySQLDatabaseTool(FileConfiguration config) {
        this.config = config;
        this.username = config.getString("Database.username");
        this.password = config.getString("Database.password");
        this.DBName = config.getString("Database.dbName");
        url = url.replace("%URL%", config.getString("Database.location")).replace("%DBName%", DBName);
        setupDatabase(username, password);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("LapisLogin"),
                connectionCleaning(), 30 * 20, 30 * 20);
    }

    public Runnable connectionCleaning() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void addData(String ID, String password, Long login, Long logout, String IP) {
        try {
            conn = getConnection();
            String sql = "INSERT INTO loginPlayers(UUID,Password,Login,Logout,IPAddress) VALUES(?,?,?,?,?)";
            PreparedStatement preStatement = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            preStatement.setString(1, ID);
            preStatement.setString(2, password);
            preStatement.setLong(3, login);
            preStatement.setLong(4, logout);
            preStatement.setString(5, IP);
            preStatement.execute();
            preStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setData(String ID, String path, Object data) {
        try {
            conn = getConnection();
            String sqlUpdate = "UPDATE loginPlayers SET " + path + " = ? WHERE UUID = ?";
            PreparedStatement preStatement = conn.prepareStatement(sqlUpdate);
            preStatement.setObject(1, data);
            preStatement.setString(2, ID);
            preStatement.execute();
            preStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long getLong(String UUID, String path) {
        try {
            ResultSet rs = getResults(UUID, path);
            if (!rs.isBeforeFirst()) {
                return null;
            }
            rs.next();
            return rs.getLong(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(String UUID, String path) {
        try {
            ResultSet rs = getResults(UUID, path);
            if (!rs.isBeforeFirst()) {
                return null;
            }
            rs.next();
            return rs.getString(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getObject(String UUID, String path) {
        try {
            ResultSet rs = getResults(UUID, path);
            if (!rs.isBeforeFirst()) {
                return null;
            }
            rs.next();
            return rs.getObject(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ResultSet getResults(String UUID, String item) {
        try {
            conn = getConnection();
            String sql = "SELECT " + item + " FROM loginPlayers WHERE UUID = ?";
            PreparedStatement preStatement = conn.prepareStatement(sql);
            preStatement.setString(1, UUID);
            ResultSet rs = preStatement.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dropRow(String UUID) {
        try {
            conn = getConnection();
            String sqlUpdate = "DELETE FROM loginPlayers WHERE UUID = ?";
            PreparedStatement preStatement = conn.prepareStatement(sqlUpdate);
            preStatement.setString(1, UUID);
            preStatement.execute();
            preStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllRows() {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM loginPlayers";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getRows() {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM loginPlayers";
            ResultSet rs = stmt.executeQuery(sql);
            rs.last();
            return rs.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setupDatabase(String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + DBName;
            stmt.execute(sql);
            stmt = conn.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS loginPlayers (" +
                    "UUID VARCHAR(36) NOT NULL," +
                    "Password VARCHAR(105)," +
                    "Login BIGINT," +
                    "Logout BIGINT," +
                    "IPAddress VARCHAR(15))";
            stmt.execute(sql);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                return DriverManager.getConnection(url, username, password);
            } else {
                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
