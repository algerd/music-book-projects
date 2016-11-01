
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Entity<P, C> {
    
    public abstract void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException;
    
    public abstract int getId();
    
    public abstract void setId(int id);
    
    public abstract String getName();
    
    //связанные сущности
    private final ObservableList<C> children = FXCollections.observableArrayList();
    private ObjectProperty<P> parent = new SimpleObjectProperty<>(null);

    public ObservableList<C> getChildren() {
        return children;
    }
       
    public P getParent() {
        return parent.get();
    }
    public void setParent(P value) {
        parent.set(value);
    }
    public ObjectProperty<P> parentProperty() {
        return parent;
    }
    
}
