package com.example.herokudemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

@RestController
public class IndexController {

    private static String getConnectionString() {
        String JDBC_DATABASE_URL = null;
        // First try and get the variable from the system
        JDBC_DATABASE_URL = System.getenv("JDBC_DATABASE_URL");
        // If not available, get from .env file instead
        if(JDBC_DATABASE_URL == null || JDBC_DATABASE_URL.equals("")) {
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            JDBC_DATABASE_URL = dotenv.get("JDBC_DATABASE_URL");
        }
        //TODO add exception here for not connection details
        return JDBC_DATABASE_URL;
    }

    private static String readData(Connection c) {
        String SQL = "SELECT number, name FROM test";
        try (PreparedStatement s = c.prepareStatement(SQL)) {
            ResultSet r = s.executeQuery();
            while (r.next()) {
                int id = r.getInt("number");
                String name = r.getString("name");
                return "Id: " + id + " is: " + name;
            }
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "";
    }

    @GetMapping("/")
    public String index() {
        String CS = getConnectionString();

        System.out.println(CS);

        // Try JDBC connection
        try(Connection c = DriverManager.getConnection(CS)) {
            System.out.println("Connection success");
            return readData(c);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getClass().getName() + ": " + e.getMessage();
        }
        //return "Hey there! I am running.";
    }
}
