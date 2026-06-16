package com.dandaev.edu.listener;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.dandaev.edu.util.ConnectionManager;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@ WebListener
public class AppContextListener implements ServletContextListener {

	@ Override
	public void contextInitialized (ServletContextEvent sce) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
		}

		ConnectionManager.getDataSource();
	}

	@ Override
	public void contextDestroyed (ServletContextEvent sce) {
		ConnectionManager.closePool();

		deregisterJdbcDrivers();
	}

	private void deregisterJdbcDrivers () {
		DriverManager.getDrivers()
		             .asIterator()
		             .forEachRemaining(driver -> {
			             if (driver.getClass()
			                       .getClassLoader() == getClass().getClassLoader()) {
				             try {
					             DriverManager.deregisterDriver(driver);
				             } catch (SQLException e) {
								/* TODO логировать */
				             }
			             }
		             });
	}
}
