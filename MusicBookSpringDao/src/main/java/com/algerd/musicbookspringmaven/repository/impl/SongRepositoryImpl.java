
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.repository.SongRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class SongRepositoryImpl extends CrudRepositoryImpl<Song> implements SongRepository {

    @Override
    public boolean containsAlbum(Album album) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_album = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {album.getId()}), 
            (ResultSet rs) -> rs.getInt(1)
        ) > 0;  
    }  
    
    @Override
    public List<Song> selectByAlbum(Album album) {
        String prepareQuery = "select * from song where id_album = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {album.getId()}), 
            (ResultSet rs, int rowNum) -> getEntity(rs)   
        );
    }
     
    @Override
    public List<Song> selectJoin() {
        String query = 
            "select "
                + "song.id as id, "
                + "song.id_album as id_album, "
                + "song.name as name, "
                + "song.rating as rating, "
                + "album.name as album_name, "
                + "album.year as year, "
                + "album.id_artist as id_artist, "
                + "artist.name as artist_name "
            + "from song "
                + "left join album on song.id_album = album.id "
                + "left join artist on album.id_artist = artist.id ";
             
        return jdbcTemplate.query(
            query, 
            (ResultSet resultSet, int rowNum) -> {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setId_album(resultSet.getInt("id_album"));
                song.setName(resultSet.getString("name"));
                song.setRating(resultSet.getInt("rating"));

                Album album = new Album();
                album.setId(song.getId_album());
                album.setName(resultSet.getString("album_name"));
                album.setYear(resultSet.getInt("year"));
                album.setId_artist(resultSet.getInt("id_artist"));

                Artist artist = new Artist();
                artist.setId(album.getId_artist());
                artist.setName(resultSet.getString("artist_name"));

                album.setArtist(artist);
                song.setAlbum(album);
                return song;
            }
        );  
    }
    
    @Override
    public boolean containsSong(String name, Album album) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_album = ? and name = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER, Types.VARCHAR}    
        );
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {album.getId(), name.trim()}), 
            (ResultSet rs) -> rs.getInt(1)
        ) > 0;  
    }
   
}
