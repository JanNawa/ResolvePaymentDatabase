package assignment6.database;

import assignment6.*;
import java.sql.*;
import java.util.*;

/**
 * This class connect to MySql database and extract information to data model
 *
 * @author Jan
 */
public class MySqlConnector implements DatabaseConnector {

    // create properties instance
    private Properties dbProperties = new Properties();

    // store the info from the properties file for configuration
    private String driver;
    private String url;
    private String username;
    private String password;

    public MySqlConnector(String filename, PropertiesType type) {
        dbProperties = PropertiesExtraction.extractProperties(filename, type);
        driver = dbProperties.getProperty("jdbc.driver");
        url = dbProperties.getProperty("jdbc.url");
        username = dbProperties.getProperty("jdbc.username");
        password = dbProperties.getProperty("jdbc.password");
    }

    // Get the connection
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // get the driver
    @Override
    public String getDriver() {
        return driver;
    }
}
