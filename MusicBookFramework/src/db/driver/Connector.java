
package db.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteConfig;

public class Connector {
    
    private final static Connector instance = new Connector();
    private final Driver driver = new org.sqlite.JDBC();
    private final String urlDb = "jdbc:sqlite:src//resources//dbsource//musicbook.sqlite";
    private Connection connection = null;
      
    private Connector() {}
    
    public static Connector getInstance() {
        return instance;
    }
      
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
    
    /**
     * Получить последний сгенерированный первичный ключ
     * @return int id последней вставленной записи
     * @throws SQLException 
     */
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
