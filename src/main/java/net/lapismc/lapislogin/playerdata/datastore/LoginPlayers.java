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

import net.lapismc.datastore.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginPlayers extends Table {

    public LoginPlayers() {
        super("LoginPlayers", "UUID", "Username", "LoginTime", "LogoutTime", "IPAddress");
    }

    public void createTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS LoginPlayers (" +
                    "UUID VARCHAR(36) NOT NULL," +
                    "Username VARCHAR(16)," +
                    "LoginTime BIGINT," +
                    "LogoutTime BIGINT," +
                    "IPAddress VARCHAR(15))";
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
