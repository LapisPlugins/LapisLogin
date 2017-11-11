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

import java.sql.*;

public class MySQLDatabaseTool {

    String url = "jdbc:mysql://%URL%/%DBName%";
    String username;
    String password;
    String DBName;
    Connection conn;

    public MySQLDatabaseTool(String loc, String username, String password, String DBName) {
        url.replace("%URL%", loc).replace("%DBName%", DBName);
        this.username = username;
        this.password = password;
        this.DBName = DBName;
        setupDatabase(username, password);
    }

    public void addData(String ID, Object password, Object login, Object logout, Object IP) {
        try {
            conn = getConnection();
            String sql = "INSERT INTO loginPlayers(id,password,login,logout,ip) VALUES(?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setObject(1, ID);
            pstmt.setObject(2, password);
            pstmt.setObject(3, login);
            pstmt.setObject(4, logout);
            pstmt.setObject(4, IP);
            pstmt.execute();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setData(String ID, String item, Object data) {
        try {
            conn = getConnection();
            String sqlUpdate = "UPDATE loginPlayers SET ? = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setString(1, item);
            pstmt.setObject(2, data);
            pstmt.setString(3, ID);
            pstmt.execute();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getData(String ID, String item) {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT " + item + " WHERE UUID = " + ID + " FROM loginPlayers";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.isBeforeFirst()) {
                rs.close();
                conn.close();
                return null;
            }
            rs.next();
            Object data = rs.getObject(item);
            rs.close();
            conn.close();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupDatabase(String username, String password) {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + DBName;
            stmt.execute(sql);
            stmt = conn.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS loginPlayers";
            stmt.execute(sql);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
