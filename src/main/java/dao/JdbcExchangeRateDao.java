package dao;

import Utils.DatabaseConnectionManager;
import exceptions.DatabaseOperationException;
import models.Currency;
import models.ExchangeRate;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SimpleTimeZone;

public class JdbcExchangeRateDao implements ExchangeRateDao {


    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {

        final String query = """
                        SELECT
                            er.id AS id,
                            bc.id AS base_id,
                            bc.Code AS base_code,
                            bc.FullName AS base_name,
                            bc.Sign AS base_sign,
                            tc.id AS target_id,
                            tc.Code AS target_code,
                            tc.FullName AS target_name,
                            tc.Sign AS target_sign,
                            er.rate AS rate
                        FROM Exchange_rates er
                        JOIN Currencies bc ON er.base_currency_id = bc.id
                        JOIN Currencies tc ON er.target_currency_id = tc.id
                        WHERE (
                            base_currency_id = (SELECT c.id FROM currencies c WHERE c.code = ?)
                        AND
                            target_currency_id = (SELECT c2.id FROM currencies c2 WHERE c2.code = ?)
                        )
                        """;

        try(Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return Optional.of(getExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Exchange rate with code" + baseCurrencyCode + " and currency code" + targetCurrencyCode + " not found");
        }

        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        final String query = """
                        SELECT
                            er.id AS id,
                            bc.id AS base_id,
                            bc.Code AS base_code,
                            bc.FullName AS base_name,
                            bc.Sign AS base_sign,
                            tc.id AS target_id,
                            tc.Code AS target_code,
                            tc.FullName AS target_name,
                            tc.Sign AS target_sign,
                            er.rate AS rate
                        FROM Exchange_rates er
                        JOIN Currencies bc ON er.base_currency_id = bc.id
                        JOIN Currencies tc ON er.target_currency_id = tc.id
                        """;

        try(Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                exchangeRates.add(getExchangeRate(resultSet));
            }

            return exchangeRates;

        } catch (SQLException e) {
            throw new DatabaseOperationException("All exchange rates not found");
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) {

        final String query = """
                SELECT
                    er.id AS id,
                    bc.id AS base_id,
                    bc.Code AS base_code,
                    bc.FullName AS base_name,
                    bc.Sign AS base_sign,
                    tc.id AS target_id,
                    tc.Code AS target_code,
                    tc.FullName AS target_name,
                    tc.Sign AS target_sign,
                    er.rate AS rate
                FROM Exchange_rates er
                JOIN Currencies bc ON er.base_currency_id = bc.id
                JOIN Currencies tc ON er.target_currency_id = tc.id
                WHERE er.id = ?
                """;

        try(Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return Optional.of(getExchangeRate(resultSet));
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Exchange rate with id" + id + " not found");
        }

        return Optional.empty();
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {

        final String query = "INSERT INTO Exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?) RETURNING id";

        try(Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, entity.getBaseCurrency().getId());
            preparedStatement.setInt(2, entity.getTargetCurrency().getId());
            preparedStatement.setDouble(3, entity.getRate());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                throw new DatabaseOperationException("Failed to save exchange rate with id" + entity.getId() + " to database");
            }

            entity.setId(resultSet.getInt("id"));
            return entity;

        } catch (SQLException e) {
            if(e instanceof SQLiteException){
                SQLiteException exception = (SQLiteException) e;
                if(exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new DatabaseOperationException("Exchange rate with code " + entity.getBaseCurrency().getCode() + entity.getTargetCurrency().getCode() + " already exists");
                }
            }
            throw new DatabaseOperationException("Failed to save exchange rate with id " + entity.getId() + " to database");
        }
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate entity) {

        final String query = """
                UPDATE Exchange_rates
                SET rate = ?
                WHERE base_currency_id = ? AND target_currency_id = ?
                RETURNING id
                """;

        try(Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, entity.getRate());
            preparedStatement.setInt(2, entity.getBaseCurrency().getId());
            preparedStatement.setInt(3, entity.getTargetCurrency().getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                entity.setId(resultSet.getInt("id"));
                return Optional.of(entity);
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to update exchange rate with id " + entity.getId() + " to database");
        }

        return Optional.empty();
    }

    private static ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
            resultSet.getInt("id"),
            new Currency(
                resultSet.getInt("base_id"),
                resultSet.getString("base_code"),
                resultSet.getString("base_name"),
                resultSet.getString("base_sign")
            ),
            new Currency(
                resultSet.getInt("target_id"),
                resultSet.getString("target_code"),
                resultSet.getString("target_name"),
                resultSet.getString("target_sign")
            ),
            resultSet.getDouble("rate")
        );
    }
}
