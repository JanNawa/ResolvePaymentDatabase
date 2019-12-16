package assignment6.database;

import java.sql.*;

/**
 * interface for database connector to interact with database provider
 * @author Jan
 */
public interface DatabaseConnector {
    // get Connection
    public Connection getConnection() throws SQLException;
    
    // get Driver
    public String getDriver();
}