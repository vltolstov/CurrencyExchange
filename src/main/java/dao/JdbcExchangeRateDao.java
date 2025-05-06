package dao;

import Utils.DatabaseConnectionManager;
import exceptions.DatabaseOperationException;
import models.Currency;
import models.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
        return List.of();
    }

    @Override
    public Optional<ExchangeRate> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        return null;
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate entity) {
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
