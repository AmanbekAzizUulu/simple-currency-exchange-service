package com.dandaev.edu.dao;

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
import com.dandaev.edu.exception.database.UniqueConstraintViolationException;
import com.dandaev.edu.model.Currency;
import com.dandaev.edu.util.ConnectionManager;

public class CurrencyDaoImpl implements CurrencyDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDaoImpl.class);

	private static final String FIND_ALL = """
												SELECT
													id,
													code,
													full_name,
													sign
												FROM
													currencies
											""";

	private static final String FIND_BY_ID = """
												SELECT
													id,
													code,
													full_name,
													sign
												FROM
													currencies
												WHERE
													id = ?
											""";

	private static final String FIND_BY_CODE = """
													SELECT
														id,
														code,
														full_name,
														sign
													FROM
														currencies
													WHERE
														UPPER(code) = UPPER(?)
												""";
	private static final String INSERT = """
											INSERT INTO
														currencies (
																		code,
																		full_name,
																		sign
																	)
											VALUES (
														?,
														?,
														?)
										""";
	@ Override
	public List<Currency> findAll () {
		List<Currency> currencies = new ArrayList<>();
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_ALL); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				currencies.add(mapRow(rs));
			}
		} catch (SQLException e) {
			LOGGER.error("Error fetching all currencies", e);
		}
		return currencies;
	}

	@ Override
	public Optional<Currency> findById (Long id) {
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Error fetching currency by id: {}", id, e);
		}
		return Optional.empty();
	}

	@ Override
	public Optional<Currency> findByCode (String code) {
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_CODE)) {
			stmt.setString(1, code);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Error fetching currency by code: {}", code, e);
		}
		return Optional.empty();
	}

	@ Override
	public Currency persist (Currency currency) {
		try (Connection conn = ConnectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, currency.getCode().toUpperCase());
			stmt.setString(2, currency.getFullName());
			stmt.setString(3, currency.getSign());
			stmt.executeUpdate();
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					currency.setId(generatedKeys.getLong(1));
				}
			}
			return currency;
		} catch (SQLException e) {
			LOGGER.error("SQLState: {}", e.getSQLState(), e);
			if ("23505".equals(e.getSQLState()) || e.getMessage().contains("duplicate key")) {
				throw new UniqueConstraintViolationException("Currency with code '" + currency.getCode() + "' already exists", e
				);
			}
			throw new DataAccessException("Failed to save currency", e);
		}
	}

	private Currency mapRow (ResultSet rs) throws SQLException {
		Currency currency = new Currency();

		currency.setId(rs.getLong("id"));
		currency.setCode(rs.getString("code"));
		currency.setFullName(rs.getString("full_name"));
		currency.setSign(rs.getString("sign"));

		return currency;
	}
}
