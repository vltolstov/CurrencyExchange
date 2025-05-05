package dao;

import Utils.DatabaseConnectionManager;
import examples.ExampleGetEntity;
import models.Currency;
import exceptions.DatabaseOperationException;
import exceptions.EntityExistException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDao {

    @Override
    public Optional<Currency> findByCode(String code) {

        final String query = "SELECT * FROM Currencies WHERE Code = ?";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Currency with code " + code + " not found");
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
                return Optional.of(getCurrency(resultSet));
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Currency with id " + integer + " not found");
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
                currencies.add(getCurrency(resultSet));
            }

            return currencies;

        } catch (SQLException e) {
            throw new DatabaseOperationException("All currencies not found");
        }
    }

    @Override
    public Currency save(Currency entity) {

        final String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                throw new DatabaseOperationException("Failed to save currency with code " + entity.getCode() + " to database");
            }

            return getCurrency(resultSet);

        } catch (SQLException e) {
            if(e instanceof SQLiteException) {
                SQLiteException exception = (SQLiteException) e;
                if (exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistException("Currency code " + entity.getCode() + " already exists");
                }
            }
            throw new DatabaseOperationException("Failed to save currency with code " + entity.getCode() + " to database");
        }
    }

    @Override
    public Optional<Currency> update(Currency entity) {

        final String query = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ?";

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.setInt(4, entity.getId());

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to update currency with code " + entity.getCode());
        }

        return Optional.empty();
    }

    private static Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("ID"),
                resultSet.getString("Code"),
                resultSet.getString("FullName"),
                resultSet.getString("Sign"));
    }
}
