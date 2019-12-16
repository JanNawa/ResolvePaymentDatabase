package assignment6;

import assignment6.database.*;
import static assignment6.database.PropertiesType.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Testing class
 * @author Jan
 */
public class Assignment6Testing {

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {
        // create database connector
        DatabaseConnector databaseConnector = new MySqlConnector("mySqlDatabase.properties", CONFIGURATION);
        
        // connect to database
        try (Connection connection = databaseConnector.getConnection()) {
            if (databaseConnector.getDriver() != null) {
                Class.forName(databaseConnector.getDriver());
            }
            
            PaymentManagement pm = new PaymentManagement("mySqlPaymentManagement.properties", PAYMENT_MANAGEMENT);
            pm.reconcilePayments(connection);
            System.out.println("unpaidOrders: " + pm.unpaidOrders(connection));
            System.out.println("unknown payment: " + pm.unknownPayments(connection));
            
            ArrayList<Integer> paidOrder1 = new ArrayList<>();
            paidOrder1.add(10298);
            ArrayList<Integer> unpaidOrder1 = new ArrayList<>();
            unpaidOrder1.add(10128);

            System.out.println(pm.payOrder(connection, 6066.78f, "AAABBBCCC1", paidOrder1)); // 1 false (same amount w paid order)
            System.out.println(pm.payOrder(connection, 6066f, "AAABBBCCC2", paidOrder1)); // 1 false (diff amount w paid order)
            System.out.println(pm.payOrder(connection, 100f, "AAABBBCCC3", unpaidOrder1)); // 1 false (diff amount w unpaid order)
            System.out.println(pm.payOrder(connection, 13884.99f, "AAABBBCCC4", unpaidOrder1)); // 1 true (same amount w unpaid order)

            ArrayList<Integer> paidOrder2 = new ArrayList<>();
            paidOrder2.add(10345);
            paidOrder2.add(10346);
            ArrayList<Integer> unpaidOrder2 = new ArrayList<>();
            unpaidOrder2.add(10421);
            paidOrder2.add(10422);
            ArrayList<Integer> unpaidOrder = new ArrayList<>();
            unpaidOrder.add(10156);
            unpaidOrder.add(10190);

            System.out.println(pm.payOrder(connection, 15867.26f, "XXXYYYZZZ1", paidOrder2)); // 2 false (same amount w paid order w diff customer)
            System.out.println(pm.payOrder(connection, 15867f, "XXXYYYZZZ2", paidOrder2)); // 2 false (diff amount w paid order w diff customer)
            System.out.println(pm.payOrder(connection, 50f, "XXXYYYZZZ3", unpaidOrder2)); // 2 false (diff amount w unpaid order w diff customer)
            System.out.println(pm.payOrder(connection, 13488.54f, "XXXYYYZZZ4", unpaidOrder2)); // 2 false (same amount w unpaid order w diff customer)
            System.out.println(pm.payOrder(connection, 50f, "XXXYYYZZZ5", unpaidOrder)); // 2 false (diff amount w unpaid order w same customer) 
            System.out.println(pm.payOrder(connection, (float)15321.38, "XXXYYYZZZ6", unpaidOrder)); // 2 true (same amount w unpaid order w same customer)            
        } catch (SQLException ex) {
            System.out.println("Error on SQL");
            Logger.getLogger(MySqlConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
