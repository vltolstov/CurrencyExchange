package examples;

import Utils.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Example {

    public static void main(String[] args) {

        final String query = "SELECT * FROM Currencies WHERE ID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, 1);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                System.out.println(resultSet.getString("ID"));
                System.out.println(resultSet.getString("Code"));
                System.out.println(resultSet.getString("FullName"));
                System.out.println(resultSet.getString("Sign"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
