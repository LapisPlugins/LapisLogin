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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MySQLDataStore extends DataStore {

    protected final LapisLogin plugin;
    private final String url;
    Connection conn;
    Long connectionTime;
    boolean checkingForConversion = false;
    private String DBName;
    private String username;
    private String password;

    MySQLDataStore(LapisLogin plugin, String url) {
        this.plugin = plugin;
        this.url = url;
    }

    public MySQLDataStore(LapisLogin plugin, boolean checkingForConversion) {
        this.checkingForConversion = checkingForConversion;
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.username = config.getString("Database.username");
        this.password = config.getString("Database.password");
        this.DBName = config.getString("Database.dbName");
        url = "jdbc:mysql://%URL%/%DBName%?verifyServerCertificate=false&useSSL=true".replace("%URL%", config.getString("Database.location")).replace("%DBName%", DBName);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        connectionTime = System.currentTimeMillis();
        if (!checkingForConversion) {
            setupDatabase();
        }
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            try {
                if (conn == null || conn.isClosed()) {
                    return;
                }
                if (System.currentTimeMillis() - connectionTime >= TimeUnit.MINUTES.toMillis(5)) {
                    closeConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 20 * 60, 20 * 10);
    }

    public MySQLDataStore(LapisLogin p) {
        this(p, false);
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addData(String valueNames, String values) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String valuesQuery = getQuery(valueNames);
                String sql = "INSERT INTO LoginPlayers(" + valueNames + ") VALUES(" + valuesQuery + ")";
                PreparedStatement preStatement = getConnection(false).prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
                int i = 1;
                for (String s : values.split("#")) {
                    preStatement.setString(i, s);
                    i++;
                }
                preStatement.execute();
                preStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private String getQuery(String values) {
        StringBuilder query = new StringBuilder();
        String[] valuesArray = values.split(",");
        for (String ignored : valuesArray) {
            query.append("?,");
        }
        return query.toString().substring(0, query.toString().length() - 1);
    }

    public void setData(String primaryKey, String primaryValue,
                        String key, String value) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String sqlUpdate = "UPDATE LoginPlayers SET " + key + " = ? WHERE " + primaryKey + " = ?";
                PreparedStatement preStatement = getConnection(false).prepareStatement(sqlUpdate);
                preStatement.setString(1, value);
                preStatement.setString(2, primaryValue);
                preStatement.execute();
                preStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Long getLong(String primaryKey, String value, String key) {
        try {
            ResultSet rs = getResults(primaryKey, value);
            if (rs == null) {
                return null;
            }
            if (rs.isClosed()) {
                return null;
            }
            if (!rs.isBeforeFirst()) {
                return null;
            }
            try {
                rs.next();
            } catch (NullPointerException e) {
                return null;
            }
            return rs.getLong(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(String primaryKey, String value, String key) {
        try {
            ResultSet rs = getResults(primaryKey, value);
            if (rs == null) {
                return null;
            }
            if (rs.isClosed()) {
                return null;
            }
            if (!rs.isBeforeFirst()) {
                return null;
            }
            try {
                rs.next();
            } catch (NullPointerException e) {
                return null;
            }
            return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getBoolean(String primaryKey, String value, String key) {
        try {
            ResultSet rs = getResults(primaryKey, value);
            if (rs == null) {
                return null;
            }
            if (rs.isClosed()) {
                return null;
            }
            if (!rs.isBeforeFirst()) {
                return null;
            }
            try {
                rs.next();
            } catch (NullPointerException e) {
                return null;
            }
            return rs.getString(key).equals("1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getObject(String primaryKey, String value, String key) {
        try {
            ResultSet rs = getResults(primaryKey, value);
            if (rs == null) {
                return null;
            }
            if (rs.isClosed()) {
                return null;
            }
            if (!rs.isBeforeFirst()) {
                return null;
            }
            try {
                rs.next();
            } catch (NullPointerException e) {
                return null;
            }
            return rs.getObject(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getStringList(String primaryKey, String value, String key) {
        ResultSet rs = getResults(primaryKey, value);
        List<String> list = new ArrayList<>();
        try {
            if (rs == null) {
                return list;
            }
            if (rs.isClosed()) {
                return list;
            }
            try {
                while (rs.next()) {
                    list.add(rs.getString(key));
                }
            } catch (NullPointerException e) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Long> getLongList(String primaryKey, String value, String key) {
        ResultSet rs = getResults(primaryKey, value);
        List<Long> list = new ArrayList<>();
        try {
            if (rs == null) {
                return list;
            }
            if (rs.isClosed()) {
                return list;
            }
            try {
                while (rs.next()) {
                    list.add(rs.getLong(key));
                }
            } catch (NullPointerException e) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllRows(String key) {
        List<String> list = new ArrayList<>();
        ResultSet rs = getAllRows();
        try {
            if (rs == null) {
                return list;
            }
            if (rs.isClosed()) {
                return list;
            }
            try {
                while (rs.next()) {
                    list.add(rs.getString(key));
                }
            } catch (NullPointerException e) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    private ResultSet getResults(String attribute, String value) {
        try {
            String sqlUpdate = "SELECT * FROM LoginPlayers WHERE " + attribute + " = ?";
            PreparedStatement preStatement = getConnection(false).prepareStatement(sqlUpdate);
            preStatement.setString(1, value);
            return preStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeData(String attribute, String value) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String sqlUpdate = "DELETE FROM LoginPlayers WHERE " + attribute + " = ?";
                PreparedStatement preStatement = getConnection(false).prepareStatement(sqlUpdate);
                preStatement.setString(1, value);
                preStatement.execute();
                preStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void dropTable() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String sqlUpdate = "DROP TABLE LoginPlayers";
                Statement stmt = getConnection(false).createStatement();
                stmt.execute(sqlUpdate);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<String> getEntireRows() {
        List<String> list = new ArrayList<>();
        ResultSet rs = getAllRows();
        try {
            if (rs == null || !rs.isBeforeFirst()) {
                return list;
            }
            int numberOfColumns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < numberOfColumns; i++) {
                    result.append(rs.getString(i + 1));
                    if (i != numberOfColumns - 1) {
                        result.append("#");
                    }
                }
                list.add(result.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    ResultSet getAllRows() {
        try {
            boolean tableExists = true;
            if (checkingForConversion) {
                conn = DriverManager.getConnection(url.replace(DBName, ""), username, password);
                ResultSet rs = conn.createStatement().executeQuery("SELECT table_name" +
                        " FROM information_schema.tables WHERE table_schema = '" + DBName +
                        "' AND table_name = '" + Tables.LoginPlayers.getName().toLowerCase() + "'");
                tableExists = rs.next();
            }
            if (!tableExists) {
                try {
                    conn = DriverManager.getConnection(url.replace(DBName, ""), username, password);
                    return conn.createStatement().executeQuery("SELECT 1 FROM dual WHERE false");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            closeConnection();
            Statement stmt = getConnection(true).createStatement();
            String sql = "SELECT * FROM LoginPlayers";
            plugin.getLogger().info("Conn " + conn.isClosed());
            plugin.getLogger().info("Stmt " + stmt.isClosed());
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isConnected(boolean printStackTrace) {
        try {
            connectionTime = System.currentTimeMillis();
            conn = DriverManager.getConnection(url.replace(DBName, ""), username, password);
            if (checkingForConversion) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
                        + DBName + "'");
                if (rs.isBeforeFirst()) {
                    ResultSet rs1 = conn.createStatement().executeQuery("SELECT table_name" +
                            " FROM information_schema.tables WHERE table_schema = '" + DBName +
                            "' AND table_name = '" + Tables.LoginPlayers.getName().toLowerCase() + "'");
                    return rs1.next();
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (SQLException e) {
            if (printStackTrace)
                e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    void setupDatabase() {
        try {
            conn = DriverManager.getConnection(url.replace(DBName, ""), username, password);
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + DBName;
            stmt.execute(sql);
            closeConnection();
            stmt = getConnection(true).createStatement();
            sql = "CREATE TABLE IF NOT EXISTS " + Tables.LoginPlayers.getName() + " (" +
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

    Connection getConnection(boolean forceNewConnection) {
        connectionTime = System.currentTimeMillis();
        try {
            if (conn == null || conn.isClosed() || forceNewConnection) {
                conn = DriverManager.getConnection(url, username, password);
                return conn;
            } else {
                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
