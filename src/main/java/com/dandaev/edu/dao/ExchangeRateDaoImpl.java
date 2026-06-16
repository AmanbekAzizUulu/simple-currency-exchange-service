package com.dandaev.edu.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dandaev.edu.exception.database.DataAccessException;
import com.dandaev.edu.exception.database.ForeignKeyConstraintViolationException;
import com.dandaev.edu.exception.database.UniqueConstraintViolationException;
import com.dandaev.edu.model.ExchangeRate;
import com.dandaev.edu.util.ConnectionManager;

public class ExchangeRateDaoImpl implements ExchangeRateDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateDaoImpl.class);

	private static final String FIND_ALL = """
	                                           SELECT
	                                               id,
	                                               base_currency_id,
	                                               target_currency_id,
	                                               rate
	                                           FROM
	                                               exchange_rates
	                                       """;

	private static final String FIND_BY_PAIR = """
	                                               SELECT
	                                                   er.id,
	                                                   er.base_currency_id,
	                                                   er.target_currency_id,
	                                                   er.rate
	                                               FROM
	                                                   exchange_rates er
	                                               JOIN
	                                                   currencies bc ON er.base_currency_id = bc.id
	                                               JOIN
	                                                   currencies tc ON er.target_currency_id = tc.id
	                                               WHERE
	                                                   UPPER(bc.code) = UPPER(?) AND
	                                                   UPPER(tc.code) = UPPER(?)
	                                           """;

	private static final String INSERT = """
	                                         INSERT INTO
	                                             exchange_rates (base_currency_id, target_currency_id, rate)
	                                         VALUES (?, ?, ?)
	                                     """;

	private static final String UPDATE = """
	                                         UPDATE
	                                             exchange_rates
	                                         SET
	                                             rate = ?
	                                         WHERE
	                                             base_currency_id = (SELECT id FROM currencies WHERE UPPER(code) = UPPER(?)) AND
	                                             target_currency_id = (SELECT id FROM currencies WHERE UPPER(code) = UPPER(?))
	                                     """;

	@ Override
	public List<ExchangeRate> findAll () {
		List<ExchangeRate> rates = new ArrayList<>();
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_ALL); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				ExchangeRate rate = new ExchangeRate();
				rate.setId(rs.getLong("id"));
				rate.setBaseCurrencyId(rs.getLong("base_currency_id"));
				rate.setTargetCurrencyId(rs.getLong("target_currency_id"));
				rate.setRate(rs.getBigDecimal("rate"));
				rates.add(rate);
			}
		} catch (SQLException e) {
			LOGGER.error("Error fetching all exchange rates", e);
			throw new DataAccessException("Failed to fetch exchange rates", e);
		}
		return rates;
	}

	@ Override
	public Optional<ExchangeRate> findByPair (String baseCode, String targetCode) {
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_PAIR)) {
			stmt.setString(1, baseCode);
			stmt.setString(2, targetCode);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ExchangeRate rate = new ExchangeRate();
					rate.setId(rs.getLong("id"));
					rate.setBaseCurrencyId(rs.getLong("base_currency_id"));
					rate.setTargetCurrencyId(rs.getLong("target_currency_id"));
					rate.setRate(rs.getBigDecimal("rate"));
					return Optional.of(rate);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Error fetching exchange rate for pair {}/{}", baseCode, targetCode, e);
			throw new DataAccessException("Failed to fetch exchange rate", e);
		}
		return Optional.empty();
	}

	@ Override
	public ExchangeRate persist (ExchangeRate exchangeRate) {
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, exchangeRate.getBaseCurrencyId());
			stmt.setLong(2, exchangeRate.getTargetCurrencyId());
			stmt.setBigDecimal(3, exchangeRate.getRate());
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					exchangeRate.setId(keys.getLong(1));
				}
			}
			LOGGER.info("Created exchange rate: {}/{} = {}", exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId(), exchangeRate.getRate());

			return exchangeRate;
		} catch (SQLException e) {
			LOGGER.warn("Attempt to create duplicate exchange rate: baseId={}, targetId={}", exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
			var sqlState = e.getSQLState();

			if ("23505".equals(sqlState)) {
				LOGGER.warn("Foreign key violation: currency not found for baseId={} or targetId={}", exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
				throw new UniqueConstraintViolationException(String.format("Exchange rate %d -> %d already exists", exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId()), e);
			} else if ("23503".equals(sqlState)) {
				LOGGER.warn("NOT NULL constraint violation for exchange rate");
				throw new ForeignKeyConstraintViolationException(String.format("Cannot save exchange rate. One of the currencies does not exist. baseId=%d, targetId=%d", exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId()), e);
			} else if ("23502".equals(sqlState)) {
				LOGGER.error("Unexpected database error while inserting exchange rate: baseId={}, targetId={}", exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId(), e);
				throw new IllegalArgumentException("Not null constraint violation", e);
			}

			throw new RuntimeException("Failed to persist exchange rate", e);
		}
	}

	@ Override
	public ExchangeRate updateRate (String baseCode, String targetCode, BigDecimal newRate) {
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
			stmt.setBigDecimal(1, newRate);
			stmt.setString(2, baseCode);
			stmt.setString(3, targetCode);

			int updatedRows = stmt.executeUpdate();

			if (updatedRows == 0) {
				LOGGER.warn("No exchange rate found to update: {}/{}", baseCode, targetCode);
				throw new DataAccessException("Exchange rate not found for update: " + baseCode + "/" + targetCode);
			}
			LOGGER.info("Updated exchange rate for {}/{} to {}", baseCode, targetCode, newRate);
			return findByPair(baseCode, targetCode).orElseThrow( () -> new RuntimeException("Exchange rate disappeared after update"));
		} catch (SQLException e) {
			LOGGER.error("Error updating exchange rate for {}/{}", baseCode, targetCode, e);
			throw new DataAccessException("Failed to update exchange rate", e);
		}
	}

}
