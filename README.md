# Resolve Payment Database
This program uses to match up the payment to the order from the existing database.
Additional fields are added to some tables. Solution designs to have minimum change in database.

## Files and external data
### assignment_6 (main package for the program)
* Assignment6Testing.java --- main for the program for testing
* PaymentManagement.java --- match up the payment with order in database, also insert and query some field in database
* PropertiesExtraction.java --- extract properties file

### assignment_6.database (package for database related program)
* DatabaseConnector.java --- interface to interact with database provider
  * MySqlConnector.java --- connect to mySql database
* PropertiesType.java --- Enumeration for classify the type of properties file

### external.configuration (package for configuration)
* mySqlDatabase.properties --- mySql database configuration

### external.mySql (package for mySql code)
* mySqlPaymentManagement.properties --- mySql for PaymentManagement class

## Data structures and their relations to each other
### orders table
* add totalAmount : to easily retrieve the overall information about that orders 
			without joining with the order details
* add checkNumber : to match up cheque with the order.
			This gives the ability to match up 1 cheque with multiple orders.
			The checkNumber need to be in payments table.
### payments table
* add matchOrder : to see whether payment is match with the order.
  * YES = match
  * NO = none match

## Assumptions
* if order already paid, it can't be paid again.
* all payment that add in payOrder() is new payment, not an existing one.
* paymentDate will be the date that the payment is updated in the database 
	(not good in business, but no information here).

## Choices
* store username and password in the properties file which is separated from the program
(for security reason and the code to be more mobile and flexible)
* store mySql code in the properties file which is separated from the program
(for the code to be more mobile and flexible)

## Key algorithms and design elements
The method in PaymentManagement accept the connection from the main.
The program extract the sql from properties file for mySql code to be used in PaymentManagement's method.
* separate mySQL code from Java code --> mobility, easier to fix
* preparedStatement --> protect against SQL injection attack when parameter is needed
### reconcilePayments method
First, add columns in tables
  * add totalAmount, checkNumber (default = null) in orders table
  * add matchOrder (default = NO) in payments table
Then, calculate the totalAmount for each order and update the value in the table
Next, match up payment and order. Update the value in table.	
### payOrder method
First, run through list of orders that user input in parameter.
Get the order number and query the information associated to that order.
Check several condition:
* checkNumber in orders need to be null
* the customerNumber of order and payment need to be the same
* the sum of all orders need to be the same as the amount on cheque
If all the conditions are met, then update checkNumber in orders and add new payment to payments.
### unpaidOrders
retrieve the unpaid orders (excluded the order that is canceled and disputed) 
by finding the checkNumber that is null in orders table
### unknownPayments
retrieve the unknown payments from payments where matchOrder is 'NO'

## Limitations
* when reconcile the payment, focus on same number of total amount in order and amount in cheque and
	whether the customer is the same in order and payment. 
	1 payment can only pay 1 order, not multiple orders.
* when record payment, 1 payment can pay multiple order. 
	The amount reflect on payment is the amount on cheque, not the amount on the individual order.
	To see order amount, need to ge to orders table.
