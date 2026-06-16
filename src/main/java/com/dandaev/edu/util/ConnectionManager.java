package com.dandaev.edu.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dandaev.edu.exception.database.ConnectionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class ConnectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);
	private static HikariDataSource dataSource;

	private ConnectionManager () {
	}

	public static synchronized DataSource getDataSource () {
		if (dataSource == null) {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(PropertiesUtil.get("database.url"));
			config.setUsername(PropertiesUtil.get("database.user"));
			config.setPassword(PropertiesUtil.get("database.password"));

			config.setMaximumPoolSize(PropertiesUtil.getInt("database.pool.size", 10));
			config.setMinimumIdle(PropertiesUtil.getInt("database.pool.minIdle", 2));

			config.setConnectionTimeout(PropertiesUtil.getLong("database.connection.timeout", 30000L));
			config.setIdleTimeout(PropertiesUtil.getLong("database.idle.timeout", 600000L));
			config.setMaxLifetime(PropertiesUtil.getLong("database.max.lifetime", 1800000L));

			config.setConnectionTestQuery("SELECT 1");
			config.setPoolName("CurrencyExchangePool");

			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

			dataSource = new HikariDataSource(config);
			LOGGER.info("HikariCP pool '{}' initialized (max={})", config.getPoolName(), config.getMaximumPoolSize());
		}
		return dataSource;
	}

	public static Connection getConnection () {
		try {
			return getDataSource().getConnection();
		} catch (SQLException e) {
			LOGGER.error("Failed to obtain connection from pool", e);
			throw new ConnectionException("Cannot get database connection", e);
		}
	}

	public static void closePool () {
		if (dataSource != null && !dataSource.isClosed()) {
			LOGGER.info("Closing connection pool...");
			dataSource.close();
			dataSource = null; 
		}
	}
}
