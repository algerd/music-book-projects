
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.repository.AlbumRepository;
import java.sql.ResultSet;
import java.util.List;
import java.sql.Types;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class AlbumRepositoryImpl extends CrudRepositoryImpl<Album> implements AlbumRepository {
   
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
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {artist.getId()}), 
            (ResultSet rs, int rowNum) -> getEntity(rs)   
        );
    }   
    
    @Override
    public boolean containsArtist(Artist artist) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_artist = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        ); 
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {artist.getId()}), (ResultSet rs) -> rs.getInt(1)) > 0;  
    }
     
    @Override
    public boolean containsAlbum(String name, Artist artist) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_artist = ? and name = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER, Types.VARCHAR}    
        );
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {artist.getId(), name.trim()}), 
            (ResultSet rs) -> rs.getInt(1)
        ) > 0;  
    }
     
}
