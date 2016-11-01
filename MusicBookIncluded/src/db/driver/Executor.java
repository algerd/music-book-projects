
package db.driver;

import db.entity.Entity;
import db.table.Extractor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Executor {

    private Connector connector = new Connector();
    
    /**
     * Получить список названий полей таблицы tableName без первичного поля id.
     * @param tableName Имя таблицы
     * @return Список String имён колонок без первичной(id)
     */
    public List<String> getColumnNames(String tableName) {
        List<String> columns = new ArrayList<>();     
        String SQL = "select * from " + tableName;     
        try {                        
			ResultSet rs = connector.connect().createStatement().executeQuery(SQL);           
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 2; i <= columnCount; i++) {
                columns.add(rsmd.getColumnName(i).toLowerCase());        
			}
            connector.commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			connector.rollback();			
		}
		finally {
			connector.close();
		}
        return columns;
    }
    
    /**
     * Вставить сущность entity в таблицу бд tableName, используя PreparedStatement.
     * @param entity Вставляемая запись
     * @param tableName Имя таблицы бд
     * @param countColumns Число коронок без учёта первичной(id)
     * @return id последней вставленной записи
     */
    public  int executePrepareInsert(Entity entity, String tableName, int countColumns) {       
        String prepareStr = "";
        for (int i = 0; i < countColumns; ++i) {
            prepareStr += ", ?";
        }      
        String SQL = "insert into " + tableName + " values (null" + prepareStr + ")";
        int lastId = 0;
		try {
            PreparedStatement pstmt = connector.connect().prepareStatement(SQL);
            entity.fillPreparedStatement(pstmt, false);
            pstmt.executeUpdate();
            lastId = connector.getGeneratedKey();
            connector.commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			connector.rollback();			
		}
		finally {
			connector.close();
		}
        return lastId;
    }
    
    /**
     * Обновить значения колонок columns сущности entity в таблице tableName, используя PreparedStatement.
     * @param entity Обновляемая запись
     * @param tableName Имя таблицы
     * @param columns Список имён колонок таблицы
     */
    public void executePrepareUpdate(Entity entity, String tableName, List<String> columns) {       
        String setString = "";
        Iterator<String> columnsIterator = columns.iterator();
        while (columnsIterator.hasNext()) {
            setString += columnsIterator.next() + " = ?";
            if (columnsIterator.hasNext()) {
                setString += ", ";
            }
        }
        String SQL = "update " + tableName + " set " + setString + " where id = ?";
		try {
            PreparedStatement pstmt = connector.connect().prepareStatement(SQL);
            entity.fillPreparedStatement(pstmt, true);
            pstmt.executeUpdate();
            connector.commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			connector.rollback();			
		}
		finally {
			connector.close();
		}
    }
    
    /**
     * Удалить запись с индексом id из таблицы tableName, используя PreparedStatement.
     * @param tableName Имя таблицы
     * @param id Индекс удаляемой записи
     */
    public void executePrepareDelete(String tableName, int id) {
        String SQL = "delete from " + tableName + " where id = ?";
		try {
            PreparedStatement pstmt = connector.connect().prepareStatement(SQL);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            connector.commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			connector.rollback();			
		}
		finally {
			connector.close();
		}
    }
    
    /**
     * Выбрать все записи из таблицы tableName и вернуть список сущностей Entity.
     * @param tableName Имя таблицы
     * @param callback Лямбда-выражение, принимающее ResultSet выбранной записи и возвращающее сущность Entity
     * @return Список List сущностей Entity
     */
    public List<Entity> executeSelect(String tableName, Extractor<ResultSet, Entity> callback) {
        List<Entity> entities = new ArrayList<>();
        String SQL = "select * from " + tableName;     
        try {                        
			ResultSet rs = connector.connect().createStatement().executeQuery(SQL);           
            while (rs.next()) {
                entities.add(callback.extract(rs));
            }                             
            connector.commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			connector.rollback();			
		}
		finally {
			connector.close();
		}
        return entities;     
    }
    
    /**
     * Выбрать запись из таблицы tableName с индексом id и вернуть сущность Entity, используя PreparedStatement.
     * @param tableName Имя талицы
     * @param callback Лямбда-выражение, принимающее ResultSet выбранной записи и возвращающее сущность Entity
     * @param id Индекс записи в таблице
     * @return Сущность Entity 
     */
    public Entity executePrepareSelect(String tableName, Extractor<ResultSet, Entity> callback, int id) {
        Entity entity = null;
        String SQL = "select * from " + tableName + " where id = ?";
        try {
            PreparedStatement pstmt = connector.connect().prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();          
            if (rs.next()) {
                entity = callback.extract(rs);
            }              
            connector.commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			connector.rollback();			
		}
		finally {
			connector.close();
		}
        return entity;
    }
      
}
