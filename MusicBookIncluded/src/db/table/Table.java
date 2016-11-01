
package db.table;

import db.driver.Executor;
import db.entity.Entity;
import db.entity.WrapUpdatedEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class Table<T extends Entity> {
    
    protected final Executor executor = new Executor();
    protected final String tableName; 
    // список имён колонок без первичной(id)
    protected final List<String> columnNames = new ArrayList<>();   
    protected ObservableList<T> entities = FXCollections.observableArrayList();
    // родительская таблица
    protected Table referenceTable;  
    // обёртка обновленного объекта
    protected final ObjectProperty<WrapUpdatedEntity<T>> updatedEntity = new SimpleObjectProperty<>();

    
    public Table(String tableName) {
        this.tableName = tableName;
        this.columnNames.addAll(executor.getColumnNames(tableName));        
    }
       
    public void setReferenceTable(Table refTable) {      
        if (refTable != null) {
            referenceTable = refTable;
            // Добавить слушателя на удаление в родительской таблице
            referenceTable.getEntities().addListener(this::deleteCascade);
            // Добавить слушателя на удаление записи в таблице
            entities.addListener(this::deleteToReferenceTable);
        }  
    }
    
    /**
     * При удалении записи в таблице удалить её объект у родительской сущности.
     */
    protected void deleteToReferenceTable(ListChangeListener.Change<? extends Entity> change) {
        while (change.next()) { 
            if (change.wasRemoved()) {
                // Определить удалённую сущность
                Entity removedEntity = change.getRemoved().get(0);
                
                // Найти родительскую сущность и удалить из неё removedEntity
                Entity parentEntity = (Entity) removedEntity.getParent();
                if (parentEntity != null) {
                    parentEntity.getChildren().remove(removedEntity);
                }
            }
        }    
    }
    
    /**
     * Каскадное удаление записей в наследуемой таблице при удалении записи в родительской таблице.
     */
    protected void deleteCascade(ListChangeListener.Change<? extends Entity> change) {
        while (change.next()) { 
            if (change.wasRemoved()) {
                // Определить удалённую сущность
                Entity removedEntity = change.getRemoved().get(0);

                // Найти сущности с родительской удалённой сущностью              
                ArrayList<T> deleteEntities = new ArrayList<>();
                for (T entity : entities) {
                    if (entity.getParent() == removedEntity) {
                        deleteEntities.add(entity);
                    }
                }
                // Удалить найденные сущности 
                Iterator<T> iterator = deleteEntities.iterator();
                while (iterator.hasNext()) {    
                    delete(iterator.next());            
                }                  
            }
        }           
    }
      
    public boolean isUniqueName(String name) {
        for (T entity : entities) {
            if (entity.getName().equalsIgnoreCase(name.trim())) {
                return false;
            }            
        }
        return true;
    }
    
    /**
     * Извлечь данные из ResultSet и создать на основе их новую сущность.
     */
    public abstract T fill(ResultSet rs) throws SQLException;
    
    /**
     * Добавить сущность в бд и в таблицу, если её ещё нет в таблице,
     * в противном случае обновить сущность в бд.
     */
    public void save(T entity) {
        // если сущность ещё не имеет id - добавлять в бд с автоинкрементацией id
        if (!entities.contains(entity) || entity.getId() == 0) { 
            int countColumns = columnNames.size();
            int lastId = executor.executePrepareInsert(entity, tableName, countColumns);
            entity.setId(lastId);
            entities.add(entity);
        }
        else { 
            // сохранить изменения сущности в враппере
            WrapUpdatedEntity<T> wrapEntity = new WrapUpdatedEntity<>(select(entity.getId()), entity);
            setUpdatedEntity(wrapEntity);
            
            executor.executePrepareUpdate(entity, tableName, columnNames);                                                    
        }      
    }
   
    /**
     * Удалить сущность из бд и из таблицы.
     */
    public void delete(T entity) {
        executor.executePrepareDelete(tableName, entity.getId());
        entities.remove(entity);
    }
    
    /**
     * Извлечь все записи из бд в сущности и вернуть карту {[id]=[Entity]}.
     */
    public Map<Integer, T> select() {
        Map<Integer, T> entityMap = new HashMap<>();
        entities.clear();    
        List<Entity> entitiesList = executor.executeSelect(tableName, (ResultSet resultSet) -> fill(resultSet)); 
        for(Entity entity : entitiesList) {
            T entityTyped = (T)entity;
            entities.add(entityTyped);
            entityMap.put(entityTyped.getId(), entityTyped);          
        }
        return entityMap;
    }
    
    /**
     * Извлечь запись из бд c id и вернуть сущность T.
     */
    public T select(int id) {
        T entity = (T)executor.executePrepareSelect(tableName, (ResultSet resultSet) -> fill(resultSet), id);
        return entity;
    }
           
    @Override
    public String toString() {
        String tableString = "Table{" + "tableName=" + tableName;
        String entitiesString = "";
        Iterator<T> entitiesIterator  = entities.iterator();
        while (entitiesIterator.hasNext()) {
            entitiesString += "\n " + entitiesIterator.next();
        }     
        return tableString + entitiesString + "\n}";     
    }
    
    /**
     * Вернуть сущность с id.
     */
    public T getEntityWithId(int id) {
        for (T entity : entities) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;   
    }
    
    public String getTableName() {
        return tableName;
    }

    public ObservableList<T> getEntities() {
        return entities;
    }
    
    public List<String> getColumnNames() {
        return columnNames;
    }
      
    public WrapUpdatedEntity<T> getUpdatedEntity() {
        return updatedEntity.get();
    }

    public void setUpdatedEntity(WrapUpdatedEntity<T> value) {
        updatedEntity.set(value);
    }

    public ObjectProperty updatedEntityProperty() {
        return updatedEntity;
    }
    
}
