package org.app.fsu_bank_project.db;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.*;
import java.sql.*;

public class FSUGenBankDB {
    private Connection connection = null;
    public Statement statement;

    public FSUGenBankDB() throws SQLException {
         getConnection();
        System.out.println(connection.isClosed());
    }

    private void getConnection() {

        try {
            String url = "jdbc:mysql://localhost:3306/fsugenbank";
            String user = "root";
            String password = "Dortmund2016";
            connection = DriverManager.getConnection(url, user, password);

            String createTableSQL = "CREATE TABLE IF NOT EXISTS Dateien (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "inhalt TEXT NOT NULL" +
                    ")";
            statement = connection.createStatement();
            statement.execute(createTableSQL);
            createTableSQL = "CREATE TABLE IF NOT EXISTS USERS ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "email VARCHAR(255) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL"
                    + ")";


            statement = connection.createStatement();
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void registerUser(String email, String password) {
        String sql = "INSERT INTO USERS (email, password) VALUES ('"+email+"','"+password+"')";
        try {
            statement.execute(sql);
        }catch (Exception ignore){}
    }

    public boolean doesUserExist(String email)  {
        String sql = "SELECT COUNT(*) FROM USERS WHERE email = ?";
        PreparedStatement p = null;
        try {
            p = connection.prepareStatement(sql);
            p.setString(1,email);
            try(ResultSet rs = p.executeQuery()) {
                if(rs.next()){
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  false;
    }

    public boolean checkPassword(String email, String password){
        String sql= "SELECT Password FROM USERS WHERE email = ?";
        PreparedStatement p = null;
        String returnedPassword = "";
        try {
            p = connection.prepareStatement(sql);
            p.setString(1,email);
            try(ResultSet rs = p.executeQuery()) {
                if(rs.next()){
                    returnedPassword = rs.getString(1);
                    return returnedPassword.equalsIgnoreCase(password);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void updatePassword(String email, String newPassword){
        String sql= "UPDATE USERS SET password = ? WHERE email = ?";
        PreparedStatement p = null;
        try {
            p = connection.prepareStatement(sql);
            p.setString(1,newPassword);
            p.setString(2,email);
            p.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveFormat(String filePath, String fileName)  {
        String content = readFile(filePath);
        if (!content.isEmpty()) {
            saveFileToDatabase(fileName, content);
        }
    }


    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }


    public void saveFileToDatabase(String fileName, String content)  {
        String sql = "INSERT INTO dateien (name, inhalt) VALUES ('"+fileName+"','"+content+"')";
        System.out.println(content);
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


        public String reviewAllFormats(){
                String sql = "SELECT id, name FROM dateien";
                PreparedStatement p;
                StringBuilder output= new StringBuilder();
                int count= 1;
                try {
                    p = connection.prepareStatement(sql);
                     ResultSet resultSet = p.executeQuery();{

                        output = new StringBuilder("ID | name" + "\n");
                        while (resultSet.next()) {
                            count++;
                            int id = resultSet.getInt("id");
                            String name = resultSet.getString("name");
                            output.append(id).append(" | ").append(name).append("\n");
                        }
                        output.append("finished");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            return output.toString();
        }

    public void formatCount(int count){

    }


    public File retrieveFile(String filename) {
        String sql = "SELECT inhalt FROM dateien WHERE name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, filename);
             ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String inhalt = rs.getString("inhalt");
                    File file = new File(filename);

                    FileOutputStream outputStream = new FileOutputStream(file);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    bw.write(inhalt);
                    bw.flush();
                    bw.close();
                    return file;
                }

        } catch (IOException e){
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean nameExist(String fileName){
        String sql = "SELECT COUNT(*) FROM dateien WHERE name = ?";
        PreparedStatement p = null;
        try {
            p = connection.prepareStatement(sql);
            p.setString(1,fileName);
            try(ResultSet rs = p.executeQuery()) {
                if(rs.next()){
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  false;
    }

    public void sendEmail(String recipient, String password) {
        // Sender's email ID needs to be mentioned
        final String from = "bitraytech5@gmail.com";
        final String emailPassword = "Dortmund2024!";
        // Get system properties
        Properties properties = new Properties();

        // Setup mail server
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, emailPassword);
                    }
                });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            // Set Subject: header field
            message.setSubject("Your FSUGenBank Password");

            // Now set the actual message
            message.setText("Here is your FSUGenBank password: " + password);
            System.out.println("Message will be sent ");
            // Send message
            Transport.send(message);
            System.out.println("Message was sent successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
            System.out.println("Message error: "+ mex.getMessage());
        }
    }
}
