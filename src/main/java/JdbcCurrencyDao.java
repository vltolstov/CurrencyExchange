import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDAO {

    @Override
    public Optional<Currency> findByCode(String code) {

        final String query = "SELECT * FROM Currencies WHERE Code = ?";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return Optional.of(ExampleGetEntity.view(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Currency> findById(Integer integer) {

        final String query = "SELECT * FROM Currencies WHERE ID = ?";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, integer);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return Optional.of(ExampleGetEntity.view(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {

        List<Currency> currencies = new ArrayList<>();
        final String query = "SELECT * FROM Currencies";

        try(Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                currencies.add(ExampleGetEntity.view(resultSet));
            }

            return currencies;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void save(Currency entity) {

        final String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Currency entity) {

        final String query = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ?";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.setInt(4, entity.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
