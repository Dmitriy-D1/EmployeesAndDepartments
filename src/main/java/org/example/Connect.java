package org.example;

import java.sql.*;

public class Connect {

    private static final String URL_KEY = "url";
    private static final String USER_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    protected static Connection getConnetion() throws SQLException {

        try{
            return  DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e){
            throw new RuntimeException();
        }
    }
}
