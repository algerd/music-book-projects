
package com.algerd.musicbookspringmaven.dbDriver.impl;

import com.algerd.musicbookspringmaven.dbDriver.Connectable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteConfig;

public class Connector implements Connectable {
    
    private final static Connectable instance = new Connector();
    private final Driver driver = new org.sqlite.JDBC();
    private final String urlDb = "jdbc:sqlite:src//main//resources//dbsource//musicbook.sqlite";
    private Connection connection = null;
      
    private Connector() {}
    
    public static Connectable getInstance() {
        return instance;
    }
      
    @Override
    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            DriverManager.registerDriver(driver); 
              
            SQLiteConfig config = new SQLiteConfig(); 
            // принудить использовать foreign keys при cascade delete or update
            config.enforceForeignKeys(true);                     
            connection = DriverManager.getConnection(urlDb, config.toProperties());
            
            connection.setAutoCommit(false);
        }
        return connection;
    }
    
    @Override
    public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    @Override
	public void commit() {
		try {
			if (connection != null) {
				connection.commit();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

    @Override
	public void rollback() {
		try {
			if (connection != null) {
				connection.rollback();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	} 
    
    @Override
    public int getGeneratedKey() throws SQLException {
        int lastId = 0;
        if (connection != null && !connection.isClosed()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select last_insert_rowid();");
            lastId = (rs.next()) ? rs.getInt(1) : 0;
        }
        return lastId;
    }
       
}
