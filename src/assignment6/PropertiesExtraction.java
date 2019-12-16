package assignment6;

import assignment6.database.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author Jan
 */
public class PropertiesExtraction {
    // create properties instance
    private static Properties dbProperties = new Properties();
    
    // extract data from properties file
    public static Properties extractProperties(String filename, PropertiesType type) {
        String location = "";
        switch (type) {
            case CONFIGURATION:
                location += "src/external/configuration/";
                break;
            case PAYMENT_MANAGEMENT:
                location += "src/external/mySql/";
                break;
        }

        // load properties file to extract data from it
        try (InputStream inputFile = new FileInputStream(location + filename)) {
            dbProperties.clear();
            dbProperties.load(inputFile);
        } catch (IOException ex) {
            System.out.println("Error with IO");
            Logger.getLogger(MySqlConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return dbProperties;
    }
}
