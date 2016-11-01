
package com.algerd.musicbookspringmaven.dbDriver.impl;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.algerd.musicbookspringmaven.dbDriver.ExecRepository;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import com.algerd.musicbookspringmaven.dbDriver.DataSource;

public abstract class ExecRepositoryImpl<T extends Entity> implements ExecRepository<T> {
        
    private DataSource connector;   

    @Autowired
    @Override
    public void setConnector(DataSource connector) {
        this.connector = connector;        
    }

    @Override
    public List<T> executePrepareQuery(String prepareQuery, PrepareSetter prepareSetter, Extractor<ResultSet, Entity> callback) {
        List<T> entities = new ArrayList<>();   
        try {
            PreparedStatement pstmt = connector.getConnection().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            ResultSet resultSet = pstmt.executeQuery();  
            while (resultSet.next()) {
                entities.add((T) callback.extract(resultSet));
            }              
            commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
            rollback();			
		}
		finally {
			closeConnection();
		}
        return entities;
    }
      
    @Override
    public List<T> executeQuery(String query, Extractor<ResultSet, Entity> callback) {
        List<T> entities = new ArrayList<>();     
        try {                        
			ResultSet resultSet = connector.getConnection().createStatement().executeQuery(query);           
            while (resultSet.next()) {
                entities.add((T) callback.extract(resultSet));
            }                             
            commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			rollback();			
		}
		finally {
			closeConnection();
		}
        return entities;     
    }
    
    @Override
    public List<String> fetchColumnList(String query) {
        List<String> entities = new ArrayList<>();     
        try {                        
			ResultSet resultSet = connector.getConnection().createStatement().executeQuery(query);           
            while (resultSet.next()) {
                entities.add(resultSet.getString(1));
            }                             
            commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			rollback();			
		}
		finally {
            closeConnection();
		}
        return entities;     
    }
    
    @Override
    public void executePrepareUpdate(String prepareQuery, PrepareSetter prepareSetter) {       
		try {
            PreparedStatement pstmt = connector.getConnection().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            pstmt.executeUpdate();
            commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			rollback();			
		}
		finally {
			closeConnection();
		}
    }
    
    @Override
    public int executePrepareInsert(String prepareQuery, PrepareSetter prepareSetter) {       
		int lastId = -1;
        try {
            PreparedStatement pstmt = connector.getConnection().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            pstmt.executeUpdate();
// TODO: вынести этот блок из класса или прописать в пропертях драйвера функцию "select last_insert_rowid();"
// или прописать условие проверки типа бд и в зависимости от неё получать сгенерированный id
// или вычислять последний id до вставки, инкркментировать его и принудительно его прописывать в бд - этот способ бд-независимый
            Statement stmt = connector.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("select last_insert_rowid();");
            lastId = (rs.next()) ? rs.getInt(1) : 0;
                
            commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			rollback();			
		}
		finally {
			closeConnection();
		}
        return lastId;
    }
    
    @Override
    public void executeUpdate(String query) {
        try {                        
			connector.getConnection().createStatement().executeUpdate(query);                                                  
            commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			rollback();			
		}
		finally {
			closeConnection();
		}
    }
    
    @Override
    public int fetchIntRow(String prepareQuery, PrepareSetter prepareSetter) {   
        try {
            PreparedStatement pstmt = connector.getConnection().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            ResultSet resultSet = pstmt.executeQuery();  
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }              
            commit();
        }  
        catch (SQLException e) {
			e.printStackTrace();
			rollback();			
		}
		finally {
			closeConnection();
		}
        return 0;     
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
    public void closeConnection() {
		try {
			if (connector.isConnection()) {
				connector.getConnection().close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    @Override
	public void commit() {
		try {
			if (connector.isConnection()) {
				connector.getConnection().commit();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

    @Override
	public void rollback() {
		try {
			if (connector.isConnection()) {
				connector.getConnection().rollback();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
}
