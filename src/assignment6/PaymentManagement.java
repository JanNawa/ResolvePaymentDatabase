package assignment6;

import assignment6.database.*;
import java.util.*;
import java.sql.*;

/**
 * This class match the payment in database with the order.
 *
 * @author Jan
 */
public class PaymentManagement {

    // create properties instance
    private Properties dbProperties = new Properties();
    private double delta = 0.01;

    // constructor to load all query from properties file
    public PaymentManagement(String filename, PropertiesType type) {
        dbProperties = PropertiesExtraction.extractProperties(filename, type);
    }

    // connect payments and orders in database
    public void reconcilePayments(Connection database) {
        try {
            Statement statement = database.createStatement();
            // choose database
            statement.execute(dbProperties.getProperty("choose.db"));
            // alter table orders
            statement.execute(dbProperties.getProperty("alter.table.orders"));
            // alter table payments
            statement.execute(dbProperties.getProperty("alter.table.payments"));

            // execute query for totalAmount
            ResultSet resultSet = statement.executeQuery(dbProperties.getProperty("query.totalAmount"));
            // assign all the value in query to totalAmount in database
            while (resultSet.next()) {
                // extract value from result set
                double totalAmount = resultSet.getDouble("totalAmount");
                int orderNumber = resultSet.getInt("orderNumber");
                // prepare sql statement for update the totalAmount in orders
                PreparedStatement prepareStatement = database.prepareStatement(dbProperties.getProperty("update.totalAmount"));
                // set value of totalAmount in table orders
                prepareStatement.setDouble(1, totalAmount);
                prepareStatement.setInt(2, orderNumber);
                prepareStatement.executeUpdate();
            }

            // execute query for checkNumber
            resultSet = statement.executeQuery(dbProperties.getProperty("query.checkNumber"));
            while (resultSet.next()) {
                // extract value from result set
                int orderNumber = resultSet.getInt("orderNumber");
                String checkNumber = resultSet.getString("checkNumber");
                // prepare sql statement for update the checkNumber in orders
                PreparedStatement prepareStatement = database.prepareStatement(dbProperties.getProperty("update.checkNumber"));
                // set value of checkNumber in table orders
                prepareStatement.setString(1, checkNumber);
                prepareStatement.setInt(2, orderNumber);
                prepareStatement.executeUpdate();

                // prepare sql statement for update the matchOrder in payments
                prepareStatement = database.prepareStatement(dbProperties.getProperty("update.matchOrder"));
                // set value of matchOrder in table payments
                prepareStatement.setString(1, checkNumber);
                prepareStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("error with SQL in reconcilePayment()");
            e.printStackTrace();
        }
    }

    // record payment in database with the given cheque number and amount that cover all the given orders
    // return true if the payment record succesfully
    // otherwise, return false
    // parameters: amount - amount of money in cheque
    //              cheque_number - number of cheque
    //              orders - list of orders to be paid by cheque    
    public boolean payOrder(Connection database, float amount, String cheque_number, ArrayList<Integer> orders) {
        try {
            Statement statement = database.createStatement();
            // choose database
            statement.execute(dbProperties.getProperty("choose.db"));
            
            float sumOrder = 0;
            int customerNumber = -1;
            // run through all the list of orders
            Iterator iterator = orders.iterator();
            while (iterator.hasNext()) {
                int orderNumber = (Integer) iterator.next();
                // prepare sql statement for query the orders of that orderNumber
                PreparedStatement prepareStatement = database.prepareStatement(dbProperties.getProperty("query.order"));
                // set value of orderNumber in prepare statement
                prepareStatement.setInt(1, orderNumber);
                ResultSet resultSet = prepareStatement.executeQuery();
                while (resultSet.next()) {
                    String chequeNumber = resultSet.getString("checkNumber");
                    int customerNumberQuery = resultSet.getInt("customerNumber");
                    // if check number is not null, means it already paid,  return false
                    if (chequeNumber != null) {
                        return false;
                    }
                    // assign customerNumber to variable to check with other later
                    if (customerNumber == -1) {
                        customerNumber = customerNumberQuery;
                        // check if number of customer is the same, if not, return false (violate the rules)
                    } else if (customerNumber != customerNumberQuery) {
                        return false;
                    }
                    // add totalAmount to sumOrder
                    float totalAmount = resultSet.getFloat("totalAmount");
                    sumOrder += totalAmount;
                }
            }
            // check if the amount is equal, if yes, update the database
            if (sumOrder == amount || Math.abs(sumOrder - amount) < delta) {
                iterator = orders.iterator();
                while (iterator.hasNext()) {
                    int orderNumber = (Integer) iterator.next();
                    // prepare sql statement for update the checkNumber in orders
                    PreparedStatement prepareStatement = database.prepareStatement(dbProperties.getProperty("update.checkNumber"));
                    // set value of checkNumber in table orders
                    prepareStatement.setString(1, cheque_number);
                    prepareStatement.setInt(2, orderNumber);
                    prepareStatement.executeUpdate();
                }
                // prepare sql statement for insert the payment in payments
                PreparedStatement prepareStatement = database.prepareStatement(dbProperties.getProperty("insert.payment"));
                // set values for prepare statement
                prepareStatement.setInt(1, customerNumber);
                prepareStatement.setString(2, cheque_number);
                Timestamp date = new Timestamp(new java.util.Date().getTime());
                prepareStatement.setTimestamp(3, date);
                prepareStatement.setFloat(4, amount);
                prepareStatement.setString(5, "YES");
                prepareStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("error with SQL in payOrder()");
            e.printStackTrace();
        }
        return false;
    }

    // find unpaid order and return the list of unpaid order
    // excluded cancelled and disputed order
    public ArrayList<Integer> unpaidOrders(Connection database) {
        ArrayList<Integer> unpaidOrders = new ArrayList<>();
        try {
            Statement statement = database.createStatement();
            // choose database
            statement.execute(dbProperties.getProperty("choose.db"));

            // execute query for finding unpaidOrders
            ResultSet resultSet = statement.executeQuery(dbProperties.getProperty("query.unpaidOrders"));
            while (resultSet.next()) {
                unpaidOrders.add(resultSet.getInt("orderNumber"));
            }
        } catch (SQLException e) {
            System.out.println("error with SQL in unpaidOrders()");
            e.printStackTrace();
        }

        return unpaidOrders;
    }

    // find unknown payment and return the list of unknown payment
    public ArrayList<String> unknownPayments(Connection database) {
        ArrayList<String> unknownPayments = new ArrayList<>();
        try {
            Statement statement = database.createStatement();
            // choose database
            statement.execute(dbProperties.getProperty("choose.db"));

            // execute query for finding unknown payment
            ResultSet resultSet = statement.executeQuery(dbProperties.getProperty("query.unknownPayments"));
            while (resultSet.next()) {
                unknownPayments.add(resultSet.getString("checkNumber"));
            }
        } catch (SQLException e) {
            System.out.println("error with SQL in unknownPayments()");
            e.printStackTrace();
        }

        return unknownPayments;
    }
}
