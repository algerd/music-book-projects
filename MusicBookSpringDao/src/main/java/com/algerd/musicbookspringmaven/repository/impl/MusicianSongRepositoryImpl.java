
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianSong;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.repository.MusicianSongRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class MusicianSongRepositoryImpl extends CrudRepositoryImpl<MusicianSong> implements MusicianSongRepository {

    @Override
    public List<MusicianSong> selectJoinByMusician(Musician musician) {
        String prepareQuery = 
            "select "
                + "musician_song.id, "
                + "musician_song.id_musician, "
                + "musician_song.id_song, "
                + "song.name, "
                + "song.rating, "
                + "album.id, "
                + "album.name, "
                + "album.year, "
                + "artist.id, "
                + "artist.name "                
            + "from musician_song "
                + "left join song on musician_song.id_song = song.id "
                + "left join album on song.id_album = album.id "
                + "left join artist on album.id_artist = artist.id "
            + "where musician_song.id_musician = ?";
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {musician.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianSong musicianSong = getEntity(resultSet);
                Song song = new Song();
                song.setId(musicianSong.getId_song());
                song.setName(resultSet.getString(4));
                song.setRating(resultSet.getInt(5));

                Album album = new Album();
                album.setId(resultSet.getInt(6));
                album.setName(resultSet.getString(7));
                album.setYear(resultSet.getInt(8));
                song.setAlbum(album);

                Artist artist = new Artist();
                artist.setId(resultSet.getInt(9));
                artist.setName(resultSet.getString(10));
                album.setArtist(artist);

                musicianSong.setSong(song);
                return musicianSong;
            }
        );
    }
    
    @Override
    public List<MusicianSong> selectJoinBySong(Song song) {
        String prepareQuery = 
            "select "
                + "musician_song.id, "
                + "musician_song.id_musician, "
                + "musician_song.id_song, "
                + "musician.name, "
                + "musician.rating "
            + "from musician_song "
                + "left join musician on musician_song.id_musician = musician.id "
            + "where musician_song.id_song = ?";  
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {song.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianSong musicianSong = getEntity(resultSet);
                Musician musician = new Musician();
                musician.setId(musicianSong.getId_musician());
                musician.setName(resultSet.getString(4));
                musician.setRating(resultSet.getInt(5));
                musicianSong.setMusician(musician);
                return musicianSong;
            }
        );
    }
    
    @Override
    public boolean containsMusicianSong(Musician musician, Song song) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_musician = ? and id_song = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER, Types.INTEGER}    
        );
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {musician.getId(), song.getId()}), 
            (ResultSet rs) -> rs.getInt(1)
        ) > 0;
    }

}
