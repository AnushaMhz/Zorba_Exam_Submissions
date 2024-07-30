package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import static java.sql.DriverManager.getConnection;

public class RailwayReservation {
    public String passengerName;
    public int passengerAge;
    public String chosenSeat;
    public String typeOfReservation;
    public int amountPaid;
    public boolean isSeniorCitizen;

    public RailwayReservation(String passengerName, int passengerAge, String chosenSeat, String typeOfReservation) {
        this.passengerName = passengerName;
        this.passengerAge = passengerAge;
        this.chosenSeat = chosenSeat;
        this.typeOfReservation = typeOfReservation;
        this.isSeniorCitizen = passengerAge > 60;
        this.amountPaid = (typeOfReservation.equalsIgnoreCase("AC")) ? 100 : 60;
    }
    public static void main(String[] args)  {
        Properties properties = new Properties();
       try(FileInputStream fileInputStream = new FileInputStream("C:\\Users\\mahar\\IdeaProjects\\RailwayTicketReservation\\src\\main\\resources\\data.properties")) {
           properties.load(fileInputStream);
       }catch (IOException e) {
           System.out.println("Cannot reas Database!");
           return;
       }

        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of passenger: ");
        String name = scanner.nextLine();

        int age = 0;
        while (age == 0) {

            System.out.println("Enter the age of the passenger: ");
            String ageIn = scanner.nextLine();

            try {
                age = Integer.parseInt(ageIn);
                if (age > 0) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please Enter a Valid Age!!");
            }
        }
        System.out.println("Enter the seat chosen: ");
        String seatNum = scanner.nextLine();

        String reservationType;
        while (true) {
            System.out.println("Enter the Reservation Type" + "AC/NON-AC" + " :");
            reservationType = scanner.nextLine();
            if (reservationType.equalsIgnoreCase("AC") || reservationType.equalsIgnoreCase("Non-AC")) {
                break;
            } else {
                System.err.println("Please enter a valid reservation type (AC/Non-AC).");
            }
        }
        RailwayReservation railwayReservation = new RailwayReservation(name, age, seatNum, reservationType);
        try(Connection connection = getConnection(user)) {
            String insertSQL = "INSERT INTO railway_reservation (passenger_name, passenger_age, chosen_seat, reservation_type, is_senior_citizen, amount_paid) VALUES (?, ?, ?, ?, ?, ?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, railwayReservation.passengerName);
                preparedStatement.setInt(2, railwayReservation.passengerAge);
                preparedStatement.setString(3, railwayReservation.chosenSeat);
                preparedStatement.setString(4, railwayReservation.typeOfReservation);
                preparedStatement.setBoolean(5, railwayReservation.isSeniorCitizen);
                preparedStatement.setInt(6, railwayReservation.amountPaid);
                preparedStatement.executeUpdate();

                System.out.println("Reservation Added Successfully.");
            }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        public void updateSenorCitizen(String passengerName, boolean isSeniorCitizen) {
            String updateSQL = "UPDATE railway_reservation SET is_senior_citizen = ? WHERE passenger_name = ?";
            try (Connection connection = getConnection(updateSQL);
                 PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                preparedStatement.setBoolean(1, isSeniorCitizen);
                preparedStatement.setString(2, passengerName);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    public void updateAmountPaid(String passengerName, double amountPaid) {
        String updateSQL = "UPDATE railway_reservation SET amount_paid = ? WHERE passenger_name = ?";
        try (Connection conn = getConnection(updateSQL);
             PreparedStatement preparedStatement = conn.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, amountPaid);
            preparedStatement.setString(2, passengerName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }

