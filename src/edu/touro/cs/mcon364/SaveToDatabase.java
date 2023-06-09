package edu.touro.cs.mcon364;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class SaveToDatabase {
    public static final Logger logger = LogManager.getLogger(SaveToDatabase.class);
    List<Email> emailList;

    public SaveToDatabase(List<Email> emailList){
        this.emailList = emailList;
    }
    //https://docs.microsoft.com/en-us/sql/connect/jdbc/step-3-proof-of-concept-connecting-to-sql-using-java?view=sql-server-2017

//https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver15
//https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
//https://stackoverflow.com/questions/1582161/how-does-a-preparedstatement-avoid-or-prevent-sql-injection
public void saveEmailBatch(){
    Map<String, String> env = System.getenv();
    String endpoint = env.get("dbendpoint");
    String username = env.get("user");
    String password = env.get("password");
    System.out.println(endpoint);
    String connectionUrl = // specifies how to connect to the database
            "jdbc:sqlserver://" + endpoint + ";"
                    + "database=Weiser_Elyah;"
                    + "user=" + username + ";"
                    + "password=" + password + ";"
                    + "encrypt=false;"
                    + "trustServerCertificate=false;"
                    + "loginTimeout=30;";
    ResultSet resultSet = null;
    // ...

    //List<Email> emails = emailList;//getScrapedEmails(); // Replace this with your logic to retrieve the emails

    //String insertSql = "INSERT INTO WebScrapedEmails (EmailID, EmailAddress, Source, TimeStamp) VALUES (?, ?, ?, ?);";
    String insertSql = "INSERT INTO WebScrapedEmails (EmailAddress, Source, TimeStamp) VALUES (?, ?, ?)";
    try (Connection connection = DriverManager.getConnection(connectionUrl);
         PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {

        for (Email email : emailList) {
            //preparedStatement.setString(1, email.getEmailID());
            preparedStatement.setString(1, email.getEmailAddress());
            preparedStatement.setString(2, email.getSource());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(email.getTimeStamp()));

            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
    logger.info("Batch saved to database");

    // ...
}

    // ...
        // Connect to your database.
        // Replace server name, username, and password with your credentials
//        public static void main(String[] args) throws ClassNotFoundException {
//            Map<String, String> env = System.getenv();
//            String endpoint = env.get("dbendpoint");
//            String username = env.get("user");
//            String password = env.get("password");
//            System.out.println(endpoint);
//            String connectionUrl = // specifies how to connect to the database
//                    "jdbc:sqlserver://" + endpoint + ";"
//                            + "database=Weiser_Elyah;"
//                            + "user=" + username + ";"
//                            + "password=" + password + ";"
//                            + "encrypt=false;"
//                            + "trustServerCertificate=false;"
//                            + "loginTimeout=30;";
//            ResultSet resultSet = null;
//            try (Connection connection = DriverManager.getConnection(connectionUrl); // AutoCloseable
//                 Statement statement = connection.createStatement();)
//            {
//                // Create and execute a SELECT SQL statement.
//                String selectSql = "SELECT TOP 10 StudentId, FirstName, LastName FROM Students"; // Guardrails
//                resultSet = statement.executeQuery(selectSql);
//
//                // Print results from select statement
//                while (resultSet.next()) {
//                    System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
//                }
//            }
//            catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//            String insertSql2 = "INSERT INTO Students (FirstName, LastName) VALUES (?, ?);";
//            try (Connection connection = DriverManager.getConnection(connectionUrl);
//                 PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql2, Statement.RETURN_GENERATED_KEYS);) {
//                {
//                    prepsInsertProduct.setString(1,"Joe");
//                    prepsInsertProduct.setString(2,"Shmoe");
//                    prepsInsertProduct.execute();
//
//                    resultSet = prepsInsertProduct.getGeneratedKeys();
//                    while (resultSet.next()) {
//                        System.out.println(resultSet.getInt(1));
//                    }
//                }} catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//
//        }


        }
//        }
//    }

