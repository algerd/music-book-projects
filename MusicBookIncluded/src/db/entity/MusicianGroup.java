
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicianGroup extends Entity {
    
    // поля таблицы musicianGroup
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final IntegerProperty idMusician = new SimpleIntegerProperty(0);      
    private final IntegerProperty idArtist = new SimpleIntegerProperty(0);
    private final StringProperty startDate = new SimpleStringProperty();
    private final StringProperty endDate = new SimpleStringProperty();
                 
    public MusicianGroup() {
        super();
    }
    
    public MusicianGroup(int id, int idMusician, int idArtist, String startDate, String endDate) {
        super();
        setId(id);
        setIdMusician(idMusician);
        setIdArtist(idArtist);
        setStartDate(startDate);
        setEndDate(endDate);
    }
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setInt(1, getIdMusician()); 
        pstmt.setInt(2, getIdArtist());
        pstmt.setString(3, getStartDate());
        pstmt.setString(4, getEndDate());
        if (update) pstmt.setInt(5, getId()); 
    }
    
    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getId() {
        return id.get();
    }
    @Override
    public void setId(int value) {
        id.set(value);
    }
    public IntegerProperty idProperty() {
        return id;
    }
    
    public int getIdMusician() {
        return idMusician.get();
    }
    public void setIdMusician(int value) {
        idMusician.set(value);
    }
    public IntegerProperty idMusicianProperty() {
        return idMusician;
    }
    
    public int getIdArtist() {
        return idArtist.get();
    }
    public void setIdArtist(int value) {
        idArtist.set(value);
    }
    public IntegerProperty idArtistProperty() {
        return idArtist;
    }
    
    public String getStartDate() {
        return startDate.get();
    }
    public void setStartDate(String value) {
        startDate.set(value);
    }
    public StringProperty startDateProperty() {
        return startDate;
    }
    
    public String getEndDate() {
        return endDate.get();
    }
    public void setEndDate(String value) {
        endDate.set(value);
    }
    public StringProperty endDateProperty() {
        return endDate;
    }
    
}
