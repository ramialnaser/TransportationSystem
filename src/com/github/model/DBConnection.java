package com.github.model;

import com.github.controller.*;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBConnection {
    // constructor needs a connection type argument
    public enum ConnectionType {
        ADMIN, LOGIN_PROCESS
    }

    private Connection c;
    private String url;
    private String user;
    private String password;

    public DBConnection(ConnectionType connectionType) {

        // load properties file
        Properties prop = new Properties();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("resources/properties/db.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ssl certificate config
        System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + String.format("src/resources/keystore/myKeystore"));
        System.setProperty("javax.net.ssl.trustStorePassword", prop.getProperty("trustStorePassword"));

        // depending on connection type create correct access to db
        // db credentials
        if (connectionType == ConnectionType.LOGIN_PROCESS) {
            url = prop.getProperty("database");
            user = prop.getProperty("userLoginProcess");
            password = prop.getProperty("passwordLoginProcess");
        }

        if (connectionType == ConnectionType.ADMIN) {
            url = prop.getProperty("database");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
        }

        // db connection
        try {
            c = DriverManager.getConnection(url, user, password);
            System.out.println("It's working!");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean usernameExists(String username) {

        String query = "SELECT count(*) FROM Account WHERE Username = ?";
        int count = dbValidation(query, username);

        return count > 0;
    }

    public boolean emailExists(String email) {

        String query = "SELECT count(*) FROM Account WHERE Email = ?";
        int count = dbValidation(query, email);

        return count > 0;
    }

    private int dbValidation(String query, String col) {
        int count = 0;
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, col);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Query failed.");
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public boolean addUser(String userName, String firstName, String lastName, String email, String phone, String confirmationCode) {
        boolean status = true;
        String query = "INSERT INTO Account (Username, FirstName, LastName, Email, PhoneNumber, ConfirmationCode) VALUES (?, ?, ? ,?, ?, ?)";
        String query2 = "INSERT INTO Balance (Account_Username) VALUES (?)";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userName);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, confirmationCode);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("add user failed.");
            status = false;
        }

        if (status) {
            try (PreparedStatement ps = c.prepareStatement(query2)) {
                ps.setString(1, userName);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("add balance failed.");
                status = false;
            } finally {
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return status;
    }

    public void addConfirmationCode(String email, String confirmationCode) {
        String addConfirmationCode = "UPDATE Account SET ConfirmationCode = ? WHERE Email = ?";

        try (PreparedStatement ps = c.prepareStatement(addConfirmationCode)) {
            ps.setString(2, email);
            ps.setString(1, confirmationCode);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("add confirmation code failed.");
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean setupPassword(String userName, String password) {
        boolean status = true;
        String query = "UPDATE Account SET Password = ? WHERE Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, password);
            ps.setString(2, userName);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("set password failed.");
            status = false;
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public boolean validateConfirmationCode(String userName, String confirmationCode) {
        int count = 0;
        String query = "SELECT count(*) FROM Account WHERE Username = ? && ConfirmationCode = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userName);
            ps.setString(2, confirmationCode);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Query failed.");
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return count == 1;
    }

    public void makeBooking(int amount, String account_Username, int station_From, int station_TO, int route_Id){
        String query = "INSERT INTO Booking (AMOUNT, Account_Username, Station_From, Station_To, Route_Id) VALUE (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, amount);
            ps.setString(2, account_Username);
            ps.setInt(3, station_From);
            ps.setInt(4, station_TO);
            ps.setInt(5, route_Id);

            ps.executeUpdate();
            Account.getInstance().deductFromBalance(amount);
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validateLogin(String userName, String password) {
        int count = 0;
        String query = "SELECT count(*) FROM Account WHERE Username = ? && Password = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userName);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("validate login failed.");
            ex.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count == 1;
    }
    public void setBalance(int amount, String type, String userName){
        String query;
        if(type.equals("Deposit")){
            query = "INSERT into Balance (Deposit, Account_Username) VALUE (?,?)";
        }else{
            query = "INSERT into Balance (Payment, Account_Username) VALUE (?,?)";
        }

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, amount);
            ps.setString(2, userName);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public int getValue(String Username) {
        int total = 0;
        String query = "SELECT SUM(Deposit)-SUM(Payment) from Balance where Account_Username = ?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, Username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("get value failed.");
            ex.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public GridPane getTransaction(){
        String query = "SELECT Deposit, Payment, CreationDate FROM Transportation_System_db.Balance WHERE Account_Username = ?";
        GridPane gridPane = new GridPane();
        int rowNo = 1;
        gridPane.add(new Label("Deposit"), 0,0);
        gridPane.add(new Label("Payment"), 1,0);
        gridPane.add(new Label("Time"), 2,0);
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, Account.getInstance().getAccountId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gridPane.add(new Label(String.valueOf(rs.getInt(1))), 0,rowNo);
                    gridPane.add(new Label(String.valueOf(rs.getInt(2))), 1,rowNo);
                    gridPane.add(new Label(rs.getString(3)), 2,rowNo);
                    rowNo++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return gridPane;
    }

    public GridPane getBookingHistory(){
        String query = "SELECT Station_From,Station_To, AMOUNT, Booking.Date FROM Transportation_System_db.Booking WHERE Account_Username = ?";
        GridPane gridPane = new GridPane();
        int rowNo = 1;
        gridPane.add(new Label("From"), 0,0);
        gridPane.add(new Label("To"), 1,0);
        gridPane.add(new Label("Amount"), 3,0);
        gridPane.add(new Label("Date"), 4,0);
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, Account.getInstance().getAccountId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gridPane.add(new Label(Destinations.getInstance().getStations().get(rs.getInt(1)).toString()), 0,rowNo);
                    gridPane.add(new Label(Destinations.getInstance().getStations().get(rs.getInt(2)).toString()), 1,rowNo);
                    gridPane.add(new Label(rs.getString(3)), 3,rowNo);
                    gridPane.add(new Label(rs.getString(4)), 4,rowNo);
                    rowNo++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return gridPane;
    }


    public String getRole(String userName) {
        String query = "Select ROLE from Account where Username = ?";
        String role = "";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    role = rs.getString(1);
                    System.out.println(role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return role;
    }

    public ArrayList<String> getAccountDetails(String userName) {
        ArrayList<String> userDetails = new ArrayList<>();
        String query = "Select Username, FirstName, LastName, Email, PhoneNumber, ROLE, CreationDate from Account where Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    for (int i = 1; i <= 7; i++) {
                        userDetails.add(rs.getString(i));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userDetails;
    }

    public void updateAccountDetails(String firstName, String lastName,
                                     String phoneNumber, String newPassword) {

        String query = "Update Account set firstName = ?, lastName= ?,phoneNumber = ?, password = ? where userName =?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phoneNumber);
            ps.setString(4, newPassword);
            ps.setString(5, Account.getInstance().getAccountId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Integer, Station> getStations() {
        HashMap<Integer, Station> stations = new HashMap<>();
        String query = "SELECT * FROM Transportation_System_db.Station";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stations.put(rs.getInt(1), new Station(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Query failed.");
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return stations;
    }


    public ArrayList<String> getAvailableDestination(int from) {
        System.out.println(from);
        String query = "SELECT Station.Name FROM Station WHERE StationId IN (\n" +
                "SELECT AvailableDes FROM (\n" +
                "SELECT DISTINCT Schedule.Station_To AS AvailableDes FROM Schedule WHERE Route_Id IN (\n" +
                "SELECT DISTINCT Schedule.Route_Id FROM Schedule WHERE Station_From = ?) )AS Des WHERE AvailableDes != ?)";
        ArrayList<String> availableDestination = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1,from);
            ps.setInt(2,from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    availableDestination.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return availableDestination;
    }


    public ScheduleOrganizer getRoutesFFF() {
        ScheduleOrganizer so = new ScheduleOrganizer();
        String query = "SELECT * FROM Transportation_System_db.Schedule";
        HashMap<Integer, ArrayList<ScheduledRoute>> scheduledRoutes = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    so.addToList(rs.getInt(5),new ScheduledRoute(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return so;
    }


    public void updateComplainAnswer(String answer, String id) {
        String query = "Update Complaint set answer = ?, complaintStatus = ? where ComplaintId =?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, answer);
            ps.setString(2,"Handled");
            ps.setString(3, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void makeComplain(String message){
        String query = "INSERT INTO Complaint (Message, Account_Username) VALUES (?,?)";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, message);
            ps.setString(2, Account.getInstance().getAccountId());

            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public void addEmployee(String userName, String firstName, String lastName, String email,
                            String phoneNbr, String role) {
        int confirmationCode = 0;
        int status = 1;
        String password = "Westeros@18";
        String query = "INSERT INTO Account (Username,FirstName,LastName,Email,PhoneNumber,Password,ROLE,ConfirmationCode,IsActive ) VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, userName);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, phoneNbr);
            ps.setString(6, password);
            ps.setString(7, role);
            ps.setString(8, String.valueOf(confirmationCode));
            ps.setString(9, String.valueOf(status));


                ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public ObservableList<Booking> getBookings(String sql){
        ObservableList<Booking> bookings = FXCollections.observableArrayList();

        String query = sql;

        try (PreparedStatement ps = c.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                  bookings.add(new Booking(rs.getString(1),rs.getString(2),rs.getString(3),
                          rs.getString(4),rs.getString(5),rs.getString(6),
                          rs.getString(7)));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        return bookings;
    }
    public String getCurrentRouteForDriver() {
        String delays = "Your Schedule:\n";
        String query = "SELECT StartTime, EndTime, Delay, DelayMessage, Station_From, Station_To FRom Schedule WHERE Vehicle_Id IN (SELECT VehicleId from Vehicle where Account_Username = ?)";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1,Account.getInstance().getAccountId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    delays += String.format("Start %s, End %s,  Delay %s, Message %s, From %s, To %s%n",rs.getString(1),rs.getString(2), rs.getString(3),rs.getString(4),Destinations.getInstance().getStations().get(rs.getInt(5)),Destinations.getInstance().getStations().get(rs.getInt(6)));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return delays;
    }
    public void updateDelayAndMessage(String delay, String delayMessage) {
        String query = "UPDATE Schedule SET Delay = ?, DelayMessage = ? WHERE Vehicle_Id IN (SELECT VehicleId from Vehicle where Account_Username = ?)";

        try (PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, delay);
            ps.setString(2, delayMessage);
            ps.setString(3,Account.getInstance().getAccountId());

            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<Complaint> getComplaints(String sql){
        ObservableList<Complaint> complaints = FXCollections.observableArrayList();

        String query = sql;

        try (PreparedStatement ps = c.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    complaints.add(new Complaint(rs.getString(1),rs.getString(6),rs.getString(4),rs.getString(5),
                            rs.getString(2)));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        return complaints;
    }

    public void getComplaintMessageAndAnswer(String id, JFXTextArea message, JFXTextArea answer){

        String query = "SELECT Message, Answer from Complaint where ComplaintId=?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1,id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    message.setText(rs.getString(1));
                    answer.setText(rs.getString(2));


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }
    public String getComplaintUsername(String id){

        String query = "SELECT Account_Username from Complaint where ComplaintId=?";
        String username ="";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1,id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                username = rs.getString(1);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        return username;

    }
    public ObservableList<Employee> getEmployee(String sql){
        ObservableList<Employee> employees = FXCollections.observableArrayList();

        String query = sql;

        try (PreparedStatement ps = c.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(new Employee(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),
                            rs.getString(5),rs.getString(7),rs.getString(8)));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        return employees;
    }
    public void getEmployeeInfo(String searchedUsername, JFXTextField username,JFXTextField firstName,JFXTextField lastName,
                                JFXTextField email,JFXTextField phone,JFXTextField role){

        String query = "SELECT * from Account where Username=?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1,searchedUsername);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    username.setText(rs.getString(1));
                    firstName.setText(rs.getString(2));
                    lastName.setText(rs.getString(3));
                    email.setText(rs.getString(4));
                    phone.setText(rs.getString(5));
                    role.setText(rs.getString(7));


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }
    public ObservableList<Schedule> getScheduleInfo(String sql){
        ObservableList<Schedule> schedulesInfo = FXCollections.observableArrayList();

        String query = sql;

        try (PreparedStatement ps = c.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    schedulesInfo.add(new Schedule(rs.getString(1),rs.getString(2),
                            rs.getString(3), rs.getString(4),rs.getString(5),
                            rs.getString(6),rs.getString(7), rs.getString(8),
                            rs.getString(9),rs.getString(10), rs.getString(11),
                            rs.getString(12),rs.getString(13)));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        return schedulesInfo;
    }
    public void loadASchedule(String scheduleId,JFXTextField duration,
                              JFXTextField price, JFXTextField routeID, JFXTextField routeType, JFXTextField username,
                              JFXTextField vehicleId){
        String query ="SELECT * FROM Schedule inner join Route on Route_Id= Route.IdRoute \n" +
                "inner join Vehicle on Schedule.Vehicle_Id = Vehicle.VehicleId where ScheduleId = ?";
        try (PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1,scheduleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    duration.setText(rs.getString(4));
                    price.setText(rs.getString(9));
                    routeID.setText(rs.getString(12));
                    routeType.setText(rs.getString(15));
                    username.setText(rs.getString(17));
                    vehicleId.setText(rs.getString(13));


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
    public void updateSchedule(String scheduleId, String startTime, String endTime,String duration, String price){
        String query = "Update Schedule set StartTime = ?, EndTime = ?, Duration = ?, Price = ? where ScheduleId =?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, startTime);
            ps.setString(2,endTime);
            ps.setString(3, duration);
            ps.setString(4, price);
            ps.setString(5, scheduleId);


            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void updateVehicleUsername(String vehicleId, String username){

        String query = "Update Vehicle set Account_Username = ? where VehicleId =?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2,vehicleId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void deleteAccount(String username){

        String query = "DELETE from Account where Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void setVehicleUsernameTONull(String username) {

        String query = "Update Vehicle set Account_Username = NULL where Account_Username =?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void updateAvailability(int stationId, int isAvailable, String accountID) {
        String query = "UPDATE Taxi SET TaxiStatus = ?, Station_Id = ? WHERE Account_Username = ?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, isAvailable);
            ps.setInt(2, stationId);
            ps.setString(3, accountID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String checkTaxiAvailabilities() {
        String count = "";
        String query = "SELECT TaxiStatus, Station_Id FROM Taxi WHERE Account_Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, Account.getInstance().getAccountId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if(rs.getInt(1) != 0){
                        count = "Your Status: Available in " + Destinations.getInstance().getStations().get(rs.getInt(2));
                    }else {

                        count = "Your Status: unAvailable";

                    }

                }
            }
        } catch (SQLException ex) {
            System.out.println("validate login failed.");
            ex.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
    public String checkAvailableTaxi(int stationId) {
        String count = "";
        String query = "SELECT TaxiStatus, Station_Id, Account_Username FROM Taxi where Station_Id = ? and TaxiStatus = 1";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, stationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "No taxi available", ButtonType.OK);
                    a.showAndWait();
                }else{
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Your Taxi is waiting in the requested station", ButtonType.OK);
                    a.showAndWait();
                    updateAvailability(stationId,0, rs.getString(3));
                }

            }
        } catch (SQLException ex) {
            System.out.println("validate login failed.");
            ex.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
//    public ObservableList<TaxiStation> taxiStation(String sql) {
//        ObservableList<TaxiStation> taxiS = FXCollections.observableArrayList();
//
//        String query = sql;
//
//        try (PreparedStatement ps = c.prepareStatement(query)) {
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//
//                    taxiS.add(new TaxiStation(rs.getString(1), rs.getString(2),
//                            rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)));
//
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                c.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return taxiS;
//    }

//    public void setTaxiDriver(String driverName,String stationId) {
//        String query = "INSERT INTO Taxi (Account_Username,Station_Id) VALUES (?,?)";
//
//        try (PreparedStatement ps = c.prepareStatement(query)) {
//
//            ps.setString(1, driverName);
//            ps.setString(2, stationId);
//            ps.executeUpdate();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//
//        } finally {
//            try {
//                c.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
    public void deleteTaxiDriver(String username){

        String query = "DELETE from Taxi where  Account_Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public int getStatus(String userName) {
        int isActive = 0;
        String query = "SELECT isActive FROM Account WHERE Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    isActive = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isActive;
    }
    public void setIsActive(String username) {
        String query = "UPDATE Account SET isActive = 0 WHERE Username = ?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void deleteEmployeesBalance(String username){

        String query = "DELETE from Balance where  Account_Username = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public String returnValue(String sql){

        String value="";
        try (PreparedStatement ps = c.prepareStatement(sql)) {


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    value = rs.getString(1);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return value;
    }
    public void deleteBooking(String bookingId){

        String query = "DELETE from Booking where  BookingId = ?";

        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public boolean bookingExists(String bookingId) {

        String query = "SELECT count(*) FROM Booking WHERE BookingId = ?";
        int count = dbValidation(query, bookingId);

        return count > 0;
    }
}
