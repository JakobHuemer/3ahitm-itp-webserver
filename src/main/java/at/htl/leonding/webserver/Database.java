package at.htl.leonding.webserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final int PORT = 5432;
    private static final String DB = "db";
    private static final String SERVER = "postgres";
    //    private static final String URL = "jdbc:postgresql://postgres:" + PORT + "/" + DB;
    private static final String USER = "app";
    private static final String PASSWORD = "app";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                String.format( "jdbc:postgresql://%s:%d/%s", SERVER, PORT, DB ),
                USER,
                PASSWORD
        );
    }
}
