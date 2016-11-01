
package  db.driver;

import db.entity.Entity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import db.table.Extractor;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Connect {
    
	public static Connection getConnection() throws SQLException {	
		DriverManager.registerDriver(new org.sqlite.JDBC());        
        String urlDb = "jdbc:sqlite:src//resources//dbsource//musicbook.sqlite"; 
		Connection conn = DriverManager.getConnection(urlDb);
		conn.setAutoCommit(false);
		return conn;
	}
     
    /**
     * Получить список названий полей таблицы без первичного поля id.
     */
    public static List<String> getColumnNames(String tableName) {
        List<String> columns = new ArrayList<>();     
        String SQL = "select * from " + tableName;     
        Connection conn = null;
        try {                        
            conn = getConnection();
			ResultSet rs = conn.createStatement().executeQuery(SQL);           
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 2; i <= columnCount; i++) {
                columns.add(rsmd.getColumnName(i).toLowerCase());        
			}
            //System.out.println(columns);
            commit(conn);
        }
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		}
        return columns;
    }
      
    public static void executeUpdate(String SQL) {
        Connection conn = null;
        try {                        
            conn = getConnection();
			conn.createStatement().executeUpdate(SQL);                     
            commit(conn);
        }
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		} 
    }
    
    // возвращает последний ключ вставленного id
    public static int executeInsert(String SQL) {
        int lastId = 0;
        Connection conn = null;
		try {
            conn = Connect.getConnection();          
            conn.createStatement().executeUpdate(SQL);       
            lastId = getGeneratedKey(conn);
            commit(conn);
        }    
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		}
        return lastId;  
    }
    
    public static int executePrepareInsert(Entity entity, String tableName, int countColumns) {
        
        String prepareStr = "";
        for (int i = 0; i < countColumns; ++i) {
            prepareStr += ", ?";
        }      
        String SQL = "insert into " + tableName + " values (null" + prepareStr + ")";
        int lastId = 0;
        Connection conn = null;
		try {
            conn = Connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            entity.fillPreparedStatement(pstmt, false);
            pstmt.executeUpdate();
            lastId = getGeneratedKey(conn);
            commit(conn);
        }    
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		}
        return lastId;
    }
    
    public static void executePrepareUpdate(Entity entity, String tableName, List<String> columns) {       
        String setString = "";
        Iterator<String> columnsIterator = columns.iterator();
        while (columnsIterator.hasNext()) {
            setString += columnsIterator.next() + " = ?";
            if (columnsIterator.hasNext()) {
                setString += ", ";
            }
        }
        String SQL = "update " + tableName + " set " + setString + " where id = ?";
        Connection conn = null;
		try {
            conn = Connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            entity.fillPreparedStatement(pstmt, true);
            pstmt.executeUpdate();
            commit(conn);
        }    
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		}
    }
    
    public static void executePrepareDelete(String tableName, int id) {
        String SQL = "delete from " + tableName + " where id = ?";
        Connection conn = null;
		try {
            conn = Connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            commit(conn);
        }    
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		}
    }
    
    public static List<Entity> executeSelect(String tableName, Extractor<ResultSet, Entity> callback) {
        List<Entity> entities = new ArrayList<>();
        String SQL = "select * from " + tableName;     
        Connection conn = null;
        try {                        
            conn = getConnection();
			ResultSet rs = conn.createStatement().executeQuery(SQL);           
            while (rs.next()) {
                entities.add(callback.extract(rs));
            }                             
            commit(conn);
        }
        catch (SQLException e) {
			e.printStackTrace();
			rollback(conn);			
		}
		finally {
			closeConnection(conn);
		}
        return entities;     
    }
     
    // получить последний сгенерированный первичный ключ 
    public static int getGeneratedKey(Connection conn) throws SQLException {     
        Statement stmt = conn.createStatement();                                  
        ResultSet rs = stmt.executeQuery("select last_insert_rowid();");         
        return (rs.next()) ? rs.getInt(1) : 0;
    }
    
	public static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void commit(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
