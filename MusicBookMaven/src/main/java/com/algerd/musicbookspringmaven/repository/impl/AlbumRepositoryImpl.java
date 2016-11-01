
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.impl.LabeledPreparedStatement;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.Labeledable;
import com.algerd.musicbookspringmaven.repository.AlbumRepository;
import java.sql.ResultSet;
import java.util.List;

public class AlbumRepositoryImpl extends CrudRepositoryImpl<Album> implements AlbumRepository {
    
    public AlbumRepositoryImpl() {
        super("album");
    }
   
    @Override
    public void delete(Album entity) {
        // запретить удаление записи с id = 1 (Unknown album)
        if (entity.getId() != 1) {
            super.delete(entity);
        }      
    }
  
    @Override 
    public void save(Album entity) {
        // запретить добавление/редактирование записи с id = 1 (Unknown album)
        if (entity.getId() != 1) {
            super.save(entity);
        }
    }
    
    @Override
    public List<Album> selectByArtist(Artist artist) {
        String prepareQuery = "select * from album where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        Extractor<ResultSet, Entity> extractor = resultSet -> fill(resultSet);
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }   
    
    @Override
    public boolean containsArtist(Artist artist) {
        String prepareQuery = "select count(id) from " + tableName + " where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
     
    @Override
    public boolean containsAlbum(String name, Artist artist) {
        /*
        String prepareQuery = "selectAll count(id) from " + tableName + " where id_artist = ? and name = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, artist.getId());
            prepareStmt.setString(2, name.trim());
        };
        */
        Labeledable lpstmt = new LabeledPreparedStatement();
        String prepareQuery = 
            "select count(id) from " + tableName
                + " where id_artist = " + lpstmt.get("id_artist") + " and name = " + lpstmt.get("name");
        PrepareSetter prepareSetter = prepareStmt -> {
            lpstmt.init(prepareStmt);    
            lpstmt.set("id_artist", artist.getId());          
            lpstmt.set("name", name.trim());
        };
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
     
}
