package examples;

import Utils.DatabaseConnectionManager;
import model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExampleGetEntity {

    public static void main(String[] args) {

        final String query = "SELECT * FROM Currencies WHERE ID = ?";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, 1);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                System.out.println(view(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Currency view(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("Id"),
                resultSet.getString("Code"),
                resultSet.getString("FullName"),
                resultSet.getString("Code")
        );
    }
}
