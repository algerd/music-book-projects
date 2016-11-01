
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianAlbum;
import com.algerd.musicbookspringmaven.repository.MusicianAlbumRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class MusicianAlbumRepositoryImpl extends CrudRepositoryImpl<MusicianAlbum> implements MusicianAlbumRepository {

    @Override
    public List<MusicianAlbum> selectJoinByMusician(Musician musician) {
        String prepareQuery = 
            "select "
                + "musician_album.id as id, "
                + "musician_album.id_musician as id_musician, "
                + "musician_album.id_album as id_album, "
                + "album.name as album_name, "
                + "album.rating as rating, "
                + "artist.id as id_artist, "               
                + "artist.name as artist_name "
            + "from musician_album "
                + "left join album on musician_album.id_album = album.id "
                + "left join artist on album.id_artist = artist.id "
            + "where musician_album.id_musician = ?"; 
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {musician.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianAlbum musicianAlbum = getEntity(resultSet);
            
                Album album = new Album();
                album.setId(musicianAlbum.getId_album());
                album.setName(resultSet.getString("album_name"));
                album.setRating(resultSet.getInt("rating"));

                Artist artist = new Artist();
                artist.setId(resultSet.getInt("id_artist"));
                artist.setName(resultSet.getString("artist_name"));

                album.setArtist(artist);
                musicianAlbum.setAlbum(album);
                return musicianAlbum;
            }
        );    
    }
    
    @Override
    public List<MusicianAlbum> selectJoinByAlbum(Album album) {
        String prepareQuery = 
            "select "
                + "musician_album.id, "
                + "musician_album.id_musician, "
                + "musician_album.id_album, "
                + "musician.name, "
                + "musician.rating "
            + "from musician_album "
                + "left join musician on musician_album.id_musician = musician.id "
            + "where musician_album.id_album = ?"; 
             
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {album.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianAlbum musicianAlbum = getEntity(resultSet);
                Musician musician = new Musician();
                musician.setId(musicianAlbum.getId_musician());
                musician.setName(resultSet.getString("name"));
                musician.setRating(resultSet.getInt("rating"));
                musicianAlbum.setMusician(musician);
                return musicianAlbum;
            }
        );   
    }
    
    @Override
    public boolean containsMusicianAlbum(Musician musician, Album album) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_musician = ? and id_album = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER, Types.INTEGER}    
        );
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {musician.getId(), album.getId()}), 
            (ResultSet rs) -> rs.getInt(1)
        ) > 0;  
    }
    
}
