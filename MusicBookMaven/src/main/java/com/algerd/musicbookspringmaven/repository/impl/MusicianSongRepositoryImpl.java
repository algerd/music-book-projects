
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianSong;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.repository.MusicianSongRepository;
import java.sql.ResultSet;
import java.util.List;

public class MusicianSongRepositoryImpl extends CrudRepositoryImpl<MusicianSong> implements MusicianSongRepository {
    
    public MusicianSongRepositoryImpl() { 
        super("musician_song");
    }
    
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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianSong musicianSong = fill(resultSet);
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
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, song.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianSong musicianSong = fill(resultSet);
            Musician musician = new Musician();
            musician.setId(musicianSong.getId_musician());
            musician.setName(resultSet.getString(4));
            musician.setRating(resultSet.getInt(5));
            musicianSong.setMusician(musician);
            return musicianSong;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
    @Override
    public boolean containsMusicianSong(Musician musician, Song song) {
        String prepareQuery = "select count(id) from " + tableName + " where id_musician = ? and id_song = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId());
            prepareStmt.setInt(2, song.getId());
        };         
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }

}
