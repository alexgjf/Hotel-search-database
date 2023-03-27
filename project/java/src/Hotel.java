/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 */
public class Hotel {
    /*
    // cls comment
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec("cmd.exe /c cls");
    System.out.println(p.toString());
     */

    // reference to physical database connection.
    private Connection _connection = null;

    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

    /**
     * Creates a new instance of Hotel
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

        System.out.print("Connecting to database...");
        try {
            // constructs the connection URL
            String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
            System.out.println("Connection URL: " + url + "\n");

            // obtain a physical connection
            this._connection = DriverManager.getConnection(url, user, passwd);
            System.out.println("Done");
        } catch (Exception e) {
            System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
            System.out.println("Make sure you started postgres on this machine");
            System.exit(-1);
        }//end catch
    }//end Hotel

    // Method to calculate euclidean distance between two latitude, longitude pairs.
    public double calculateDistance(double lat1, double long1, double lat2, double long2) {
        double t1 = (lat1 - lat2) * (lat1 - lat2);
        double t2 = (long1 - long2) * (long1 - long2);
        return Math.sqrt(t1 + t2);
    }

    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate(String sql) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the update instruction
        stmt.executeUpdate(sql);

        // close the instruction
        stmt.close();
    }//end executeUpdate

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        /*
         ** obtains the metadata object for the returned result set.  The metadata
         ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCol = rsmd.getColumnCount();
        int rowCount = 0;

        // iterates through the result set and output them to standard out.
        boolean outputHeader = true;
        while (rs.next()) {
            if (outputHeader) {
                for (int i = 1; i <= numCol; i++) {
                    System.out.print(rsmd.getColumnName(i) + "\t");
                }
                System.out.println();
                outputHeader = false;
            }
            for (int i = 1; i <= numCol; ++i)
                System.out.print(rs.getString(i) + "\t");
            System.out.println();
            ++rowCount;
        }//end while
        stmt.close();
        return rowCount;
    }//end executeQuery

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        /*
         ** obtains the metadata object for the returned result set.  The metadata
         ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCol = rsmd.getColumnCount();
        int rowCount = 0;

        // iterates through the result set and saves the data returned by the query.
        boolean outputHeader = false;
        List<List<String>> result = new ArrayList<List<String>>();
        while (rs.next()) {
            List<String> record = new ArrayList<String>();
            for (int i = 1; i <= numCol; ++i)
                record.add(rs.getString(i));
            result.add(record);
        }//end while
        stmt.close();
        return result;
    }//end executeQueryAndReturnResult

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        int rowCount = 0;

        // iterates through the result set and count number of results.
        while (rs.next()) {
            rowCount++;
        }//end while
        stmt.close();
        return rowCount;
    }

    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
        Statement stmt = this._connection.createStatement();

        ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }

    public int getNewUserID(String sql) throws SQLException {
        Statement stmt = this._connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }

    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup() {
        try {
            if (this._connection != null) {
                this._connection.close();
            }//end if
        } catch (SQLException e) {
            // ignored.
        }//end try
    }//end cleanup

    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: " +
                            "java [-classpath <classpath>] " +
                            Hotel.class.getName() +
                            " <dbname> <port> <user>");
            return;
        }//end if

        Greeting();
        Hotel esql = null;
        try {
            // use postgres JDBC driver.
            Class.forName("org.postgresql.Driver").newInstance();
            // instantiate the Hotel object and creates a physical
            // connection.
            String dbname = args[0];
            String dbport = args[1];
            String user = args[2];
            esql = new Hotel(dbname, dbport, user, "");

            boolean keepon = true;
            while (keepon) {
                // These are sample SQL statements
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Create user");
                System.out.println("2. Log in");
                System.out.println("9. < EXIT");
                String authorisedUser = null;
                switch (readChoice()) {
                    case 1:
                        CreateUser(esql);
                        break;
                    case 2:
                        authorisedUser = LogIn(esql);
                        break;
                    case 9:
                        keepon = false;
                        break;
                    default:
                        System.out.println("Unrecognized choice!");
                        break;
                }//end switch
                if (authorisedUser != null) {
                    String query = "SELECT U.userType FROM Users U WHERE U.userId = " + authorisedUser;
                    String userType = esql.executeQueryAndReturnResult(query).get(0).get(0);
                    // Customer menu
                    if (userType.toLowerCase().contains("customer")) {
                        boolean usermenu = true;
                        while (usermenu) {
                            System.out.println("           MAIN MENU              ");
                            System.out.println("----------------------------------");
                            System.out.println("1. View Hotels within 30 units");
                            System.out.println("2. View Rooms");
                            System.out.println("3. Book a Room");
                            System.out.println("4. View recent booking history");
                            System.out.println("----------------------------------");
                            System.out.println("20. Log out");
                            switch (readChoice()) {
                                case 1:
                                    viewHotels(esql);
                                    break;
                                case 2:
                                    viewRooms(esql);
                                    break;
                                case 3:
                                    bookRooms(esql, authorisedUser);
                                    break;
                                case 4:
                                    viewRecentBookingsfromCustomer(esql, authorisedUser);
                                    break;
                                case 20:
                                    usermenu = false;
                                    break;
                                default:
                                    System.out.println("Unrecognized choice!");
                                    break;
                            }
                        }
                    } else {
                        // Manager menu
                        boolean usermenu = true;
                        while (usermenu) {
                            System.out.println("                  MAIN MENU                   ");
                            System.out.println("----------------------------------------------");
                            System.out.println("1. View Hotels within 30 units");
                            System.out.println("2. View Rooms");
                            System.out.println("3. Book a Room");
                            System.out.println("4. View recent booking history");
                            //the following functionalities basically only used by managers
                            System.out.println("5. Update Room Information");
                            System.out.println("6. View 5 recent Room Updates Info");
                            System.out.println("7. View booking history of the hotel");
                            System.out.println("8. View 5 regular Customers");
                            System.out.println("9. Place room repair Request to a company");
                            System.out.println("10. View room repair Requests history");

                            System.out.println("----------------------------------------------");
                            System.out.println("20. Log out");
                            switch (readChoice()) {
                                case 1:
                                    viewHotels(esql);
                                    break;
                                case 2:
                                    viewRooms(esql);
                                    break;
                                case 3:
                                    bookRooms(esql, authorisedUser);
                                    break;
                                    //this is for customer
                                case 4:
                                    viewRecentBookingsfromCustomer(esql, authorisedUser);
                                    break;
                                case 5:
                                    updateRoomInfo(esql, authorisedUser);
                                    break;
                                case 6:
                                    viewRecentUpdates(esql, authorisedUser);
                                    break;
                                case 7:
                                    viewBookingHistoryofHotel(esql);
                                    break;
                                case 8:
                                    viewRegularCustomers(esql, authorisedUser);
                                    break;
                                case 9:
                                    placeRoomRepairRequests(esql, authorisedUser);
                                    break;
                                case 10:
                                    viewRoomRepairHistory(esql, authorisedUser);
                                    break;
                                case 20:
                                    usermenu = false;
                                    break;
                                default:
                                    System.out.println("Unrecognized choice!");
                                    break;
                            }
                        }
                    }
                }
            }//end while
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            // make sure to clean up the created table and close the connection.
            try {
                if (esql != null) {
                    System.out.print("Disconnecting from database...");
                    esql.cleanup();
                    System.out.println("Done\n\nBye !");
                }//end if
            } catch (Exception e) {
                // ignored.
            }//end try
        }//end try
    }//end main

    public static void Greeting() {
        System.out.println(
                "\n\n*******************************************************\n" +
                        "                     User Interface      	               \n" +
                        "*******************************************************\n");
    }//end Greeting

    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
        int input;
        // returns only if a correct value is given.
        do {
            System.out.print("Please make your choice: ");
            try { // read the integer, parse it and break.
                input = Integer.parseInt(in.readLine());
                break;
            } catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }//end try
        } while (true);
        return input;
    }//end readChoice

    /*
     * Creates a new user
     **/
    public static void CreateUser(Hotel esql) {
        try {
            System.out.print("\tEnter name: ");
            String name = in.readLine();
            System.out.print("\tEnter password: ");
            String password = in.readLine();
            String type = "Customer";
            // Trigger here
            String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
            esql.executeUpdate(query);
            System.out.println("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end CreateUser


    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
    public static String LogIn(Hotel esql) {
        try {
            System.out.print("\tEnter userID: ");
            String userID = in.readLine();
            System.out.print("\tEnter password: ");
            String password = in.readLine();

            String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
            int userNum = esql.executeQuery(query);
            if (userNum > 0)
                return userID;
            return null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }//end

    //*********************************************
    // Rest of the functions definition go in here
    //*********************************************

    public static void viewHotels(Hotel esql) {
        try {
            // Get user input
            System.out.print("\tEnter latitude: ");
            double latitude = Double.parseDouble(in.readLine());
            System.out.print("\tEnter longitude: ");
            double longitude = Double.parseDouble(in.readLine());

            // SQL query to select hotels within 30 units of distance
            String query = String.format(
                    "SELECT * FROM Hotel H WHERE calculate_distance(%f, %f, H.latitude, H.longitude) <= 30",
                    latitude, longitude
            );

            // Execute the query and print the results
            int rowCount = esql.executeQueryAndReturnResult(query);
            if (rowCount == 0) {
                System.out.println("Sorry, no hotel found within 30 units from given place.");
            }
            // Print the query out
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            System.out.println("\t\t\tHotel:");
            for (List<String> row : result) {
                System.out.println(row.get(0) + "\t" + row.get(1));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRooms(Hotel esql) {
        try {
            // Get user input
            System.out.print("\tEnter hotel ID: ");
            int hotelID = readInput();
            System.out.print("\tEnter date (MM-dd-yyyy): ");
            String inputDate = in.readLine();

            // Check the date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            dateFormat.setLenient(false);
            Date date;
            try {
                date = dateFormat.parse(inputDate);
            } catch (ParseException e) {
                System.err.println("Invalid date format! Please enter as 'MM-dd-yyyy'.");
                return;
            }

            // Define the SQL query to select rooms with their price and availability on the given date
            // Use CASE WHEN to check if the room is available on the given date, it works like an if eles statement
            String query = String.format(
                    "SELECT R.roomNumber, R.price, " +
                            "(CASE WHEN RB.bookingID IS NULL THEN 'Available' ELSE 'Not Available' END) AS availability " +
                            "FROM Rooms R " +
                            "LEFT JOIN RoomBookings RB ON R.hotelID = RB.hotelID AND R.roomNumber = RB.roomNumber AND RB.bookingDate = '%s' " +
                            "WHERE R.hotelID = %d",
                    inputDate, hotelID
            );

            // Execute the SQL query
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            if (result.isEmpty()) {
                System.out.println("No room found for the given hotel ID and date.");
                return;
            }
            // Print the results
            System.out.println("Room Number \t | \t\t Price \t\t | \t\tAvailability");
            for (List<String> row : result) {
                System.out.println(row.get(0) + "\t\t | \t\t " + row.get(1) + "\t\t | \t\t " + row.get(2));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void bookRooms(Hotel esql, String userID) {
        try {
            // Get user ID
            int customerID = Integer.parseInt(userID);

            // get user input
            System.out.print("\tEnter hotelID: ");
            int hotelID = readInput();
            System.out.print("\tEnter room number: ");
            int roomNumber = readInput();
            System.out.print("\tEnter booking date (MM-dd-yyyy): ");
            String bookingDate = in.readLine();

            // Check if the room is available on the given date
            String checkAvailabilityQuery = String.format(
                    "SELECT * FROM RoomBookings " +
                            "WHERE hotelID = %d AND roomNumber = %d AND bookingDate = '%s'",
                    hotelID, roomNumber, bookingDate
            );
            int roomAvailability = esql.executeQuery(checkAvailabilityQuery);

            if (roomAvailability == 0) {
                // Room is available
                // Fetch the room price from the Rooms table
                String roomPriceQuery = String.format(
                        "SELECT price FROM Rooms WHERE hotelID = %d AND roomNumber = '%s'",
                        hotelID, roomNumber
                );
                List<List<String>> roomPriceResult = esql.executeQueryAndReturnResult(roomPriceQuery);
                String roomPrice = roomPriceResult.get(0).get(0);

                // Insert the booking into the RoomBookings table
                // Trigger here
                String insertBookingQuery = String.format(
                        "INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) " +
                                "VALUES (%d, %d, %d, '%s')",
                        customerID, hotelID, roomNumber, bookingDate
                );
                esql.executeUpdate(insertBookingQuery);

                // Display the room price to the customer
                System.out.println("Booking successfully! Room price: $" + roomPrice);
            } else {
                // Room is not available, display a message
                System.out.println("The room is not available on the selected date.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void updateRoomInfo(Hotel esql, String userID) {
        try {
            // Get manager ID
            int managerID = Integer.parseInt(userID);

            System.out.print("\tEnter hotelID: ");
            int hotelID = readInput();
            System.out.print("\tEnter room number: ");
            int roomNumber = readInput();

            // Check if the manager manages the hotel with the given hotelID
            String managerCheckQuery = String.format(
                    "SELECT * FROM Hotel WHERE hotelID = %d AND managerUserID = %d",
                    hotelID, managerID
            );
            int managerCheck = esql.executeQuery(managerCheckQuery);

            if (managerCheck > 0) {
                // Manager can update the room information
                // Get current room information
                String currentInfoQuery = String.format(
                        "SELECT price, imageURL FROM Rooms WHERE hotelID = %d AND roomNumber = %d",
                        hotelID, roomNumber
                );
                List<List<String>> currentInfoResult = esql.executeQueryAndReturnResult(currentInfoQuery);
                // not sure if old info is needed
                int oldPrice = Integer.parseInt(currentInfoResult.get(0).get(0));
                String oldImageURL = currentInfoResult.get(0).get(1);

                // Get the new room information
                System.out.print("\tEnter new price: ");
                int newPrice = readInput();
                System.out.print("\tEnter new image URL: ");
                String newImageURL = in.readLine();

                // Update room information in the Rooms table
                String updateRoomQuery = String.format(
                        "UPDATE Rooms SET price = %d, imageURL = '%s' WHERE hotelID = %d AND roomNumber = %d",
                        newPrice, newImageURL, hotelID, roomNumber
                );
                esql.executeUpdate(updateRoomQuery);

                // Log the update in the RoomUpdatesLog table
                // Trigger here
                String updateLogQuery = String.format(
                        "INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) " +
                                "VALUES (%d, %d, %d, 'NOW()')",
                        managerID, hotelID, roomNumber
                );
                esql.executeUpdate(updateLogQuery);

                System.out.println("Room information updated successfully!");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRecentUpdates(Hotel esql, String userID) {
        try {
            // Get manager ID
            int managerID = Integer.parseInt(userID);

            // Fetch the last 5 recent updates for the hotel
            String recentUpdatesQuery = String.format(
                    "SELECT * FROM RoomUpdatesLog " +
                            "WHERE managerID = %d ORDER BY updatedOn DESC LIMIT 5",
                    managerID
            );
            List<List<String>> recentUpdates = esql.executeQueryAndReturnResult(recentUpdatesQuery);

            if (recentUpdates.isEmpty()) {
                System.out.println("No recent updates found.");
                return;
            }

            System.out.println("Update No.\t|Manager ID\t|Hotel ID\t|Room No.\t|Update Date");
            for (List<String> row : recentUpdates) {
                System.out.println(
                        row.get(0) + "\t\t|" + row.get(1) + "\t\t|" + row.get(2) + "\t\t|" +
                                row.get(3) + "\t\t|" + row.get(4)
                );
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRecentBookingsfromCustomer(Hotel esql, String userID) {
        try {
            // Get customer ID
            int customerID = Integer.parseInt(userID);

            // Retrieve the last 5 recent bookings of the customer from the RoomBookings table
            // TODO: check this query cause I am not sure if it is correct
            String bookingHistoryQuery = String.format(
                    "SELECT RB.hotelID, RB.roomNumber, R.price, RB.bookingDate " +
                            "FROM RoomBookings RB, Rooms R " +
                            "WHERE RB.hotelID = R.hotelID AND RB.roomNumber = R.roomNumber AND RB.customerID = %d " +
                            "ORDER BY RB.bookingDate DESC LIMIT 5",
                    customerID
            );
            List<List<String>> bookingHistoryResult = esql.executeQueryAndReturnResult(bookingHistoryQuery);

            // Display the booking history
            System.out.println("**************** Your last 5 recent bookings: ****************");
            for (List<String> booking : bookingHistoryResult) {
                String hotelID = booking.get(0);
                String roomNumber = booking.get(1);
                String billingInfo = booking.get(2);
                String bookingDate = booking.get(3);

                System.out.println(
                        " --> Hotel ID: " + hotelID
                                + "\t| Room Number: " + roomNumber
                                + "\t| Billing information: " + billingInfo
                                + "\t| Booking Date: " + bookingDate
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewBookingHistoryofHotel(Hotel esql) {
        try {
            // Get the range of dates from the manager
            System.out.print("\tEnter the start date (MM-dd-yyyy): ");
            String beginDate = in.readLine();
            System.out.print("\tEnter the end date (MM-dd-yyyy): ");
            String endDate = in.readLine();

            // check the date input
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            dateFormat.setLenient(false);

            try {
                Date begin = dateFormat.parse(beginDate);
                Date end = dateFormat.parse(endDate);
            } catch (Exception e) {
                System.err.println("Invalid date format.");
                return;
            }

            // Retrieve the booking information from the RoomBookings table within the date range
            // TODO: check this query cause I am not sure if it is correct as customerID in RoomBookings is int and userID in Users is string
            String bookingQuery = String.format(
                    "SELECT RB.bookingID, U.name, RB.hotelID, RB.roomNumber, RB.bookingDate " +
                            "FROM RoomBookings RB, Users U " +
                            "WHERE RB.customerID = U.userID AND RB.bookingDate BETWEEN '%s' AND '%s' " +
                            "ORDER BY RB.bookingDate",
                    beginDate, endDate
            );
            List<List<String>> bookingResult = esql.executeQueryAndReturnResult(bookingQuery);

            // Display the booking information
            System.out.println("**************** Booking information: ****************");
            for (List<String> booking : bookingResult) {
                String bookingID = booking.get(0);
                String customerName = booking.get(1);
                String hotelID = booking.get(2);
                String roomNumber = booking.get(3);
                String bookingDate = booking.get(4);

                System.out.println(" --> Booking ID: " + bookingID
                        + ", Customer Name: " + customerName
                        + ", Hotel ID: " + hotelID
                        + ", Room Number: " + roomNumber
                        + ", Booking Date: " + bookingDate
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRegularCustomers(Hotel esql, String userID) {
        try {
            // Get the manager ID
            int managerID = Integer.parseInt(userID);

            // Get the hotelID from the manager
            System.out.print("\tEnter the hotel ID: ");
            int hotelID = readInput();

            // Check if the manager is managing the given hotel
            String hotelQuery = String.format(
                    "SELECT * FROM Hotel WHERE hotelID = %d AND managerUserID = %d",
                    hotelID, managerID
            );
            int hotelCount = esql.executeQuery(hotelQuery);

            if (hotelCount == 0) {
                System.out.println("You do not manage this hotel.");
                return;
            }

            // Retrieve the top 5 customers with the most bookings in the given hotel
            // TODO: check this query cause I am not sure if it is correct as customerID in RoomBookings is int and userID in Users is string
            String customerQuery = String.format(
                    "SELECT U.userID, U.name, COUNT(RB.bookingID) as bookings " +
                            "FROM Users U, RoomBookings RB " +
                            "WHERE U.userID = RB.customerID AND RB.hotelID = %d " +
                            "GROUP BY U.userID, U.name " +
                            "ORDER BY bookings DESC LIMIT 5",
                    hotelID
            );
            List<List<String>> customerResult = esql.executeQueryAndReturnResult(customerQuery);

            // Display the top 5 customers
            System.out.println("******* Top 5 regular customers: ********");
            for (List<String> customer : customerResult) {
                String customerID = customer.get(0);
                String customerName = customer.get(1);
                String bookings = customer.get(2);

                System.out.println(" --> Customer ID: " + customerID +
                        "\t| Customer Name: " + customerName +
                        "\t| No. of Booking: " + bookings
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void placeRoomRepairRequests(Hotel esql, String userID) {
        try {
            // Get the manager ID
            int managerID = Integer.parseInt(userID);

            // Get the hotelID, roomNumber, and companyID from the manager
            System.out.print("\tEnter the hotel ID: ");
            int hotelID = readInput();
            System.out.print("\tEnter the room number: ");
            int roomNumber = readInput();
            System.out.print("\tEnter the company ID: ");
            int companyID = readInput();

            // Check if the manager is managing the given hotel
            String hotelQuery = String.format(
                    "SELECT * FROM Hotel WHERE hotelID = %d AND managerUserID = %d",
                    hotelID, managerID
            );
            int hotelCount = esql.executeQuery(hotelQuery);

            if (hotelCount == 0) {
                System.out.println("You do not manage this hotel.");
                return;
            }

            // Get the current date
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String currentDate = sdf.format(new Date());

            // Insert the repair into the RoomRepairs table
            // Trigger here
            String insertRepairQuery = String.format(
                    "INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairDate) VALUES (%d, %d, %d, '%s')",
                    companyID, hotelID, roomNumber, currentDate
            );
            esql.executeUpdate(insertRepairQuery);

            // Get the inserted repair's repairID
            String repairIDQuery = String.format(
                    "SELECT repairID FROM RoomRepairs WHERE companyID = %d AND hotelID = %d AND roomNumber = %d ORDER BY repairID DESC LIMIT 1",
                    companyID, hotelID, roomNumber
            );
            int repairID = Integer.parseInt(esql.executeQueryAndReturnResult(repairIDQuery).get(0).get(0));

            // Insert the repair request into the RoomRepairRequests table
            //Trigger here
            String insertRequestQuery = String.format(
                    "INSERT INTO RoomRepairRequests (managerID, repairID) VALUES (%d, %d)",
                    managerID, repairID
            );
            esql.executeUpdate(insertRequestQuery);

            System.out.println("Repair request placed successfully.");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRoomRepairHistory(Hotel esql, String userID) {
        try {
            // Get the manager ID
            int managerID = Integer.parseInt(userID);

            // Fetch room repair history for the hotels
            String repairHistoryQuery = String.format(
                    "SELECT RR.companyID, RR.hotelID, RR.roomNumber, RR.repairDate " +
                            "FROM RoomRepairs RR " +
                            "JOIN RoomRepairRequests RRR ON RR.repairID = RRR.repairID " +
                            "JOIN Hotel H ON RR.hotelID = H.hotelID " +
                            "WHERE H.managerUserID = %d " +
                            "ORDER BY RR.repairDate DESC",
                    managerID
            );

            List<List<String>> repairHistory = esql.executeQueryAndReturnResult(repairHistoryQuery);

            if (repairHistory.isEmpty()) {
                System.out.println("No room repair history found.");
                return;
            }

            // Display the repair history
            System.out.println("Company ID | Hotel ID | Room No. | Repair Date");
            for (List<String> row : repairHistory) {
                System.out.println(row.get(0) + " | " + row.get(1) + " | " + row.get(2) + " | " + row.get(3));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     *  Read the input from keyboard and check if the input contains integer 0 to 9 only
     * */
    public static int readInput() {
        int intInput = 0;
        try {
            String input;
            boolean valid = false;
            do {
                input = in.readLine();
                if (input.length() == 0) {
                    System.out.println("No input. Please enter again!");
                    valid = true;
                    continue;
                }
                for (int i = 0; i < input.length(); i++) {
                    if (input.charAt(i) < '0' || input.charAt(i) > '9') {
                        System.out.println("Invalid input. Please enter again!");
                        valid = true;
                        break;
                    }
                }
            } while (valid);
            intInput = Integer.parseInt(input);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return intInput;
    }

}//end Hotel