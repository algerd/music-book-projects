
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Musician;
import db.entity.MusicianSong;
import db.entity.Song;
import java.sql.ResultSet;
import java.util.List;

public class MusicianSongTable extends Table<MusicianSong> {
    
    public MusicianSongTable() { 
        super("musician_song");
    }
    
    // вернуть ссылки, у которых есть музыкант musician
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
    
    // вернуть ссылки, у которых есть песня Song
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
    
    public boolean containsMusicianSong(Musician musician, Song song) {
        String prepareQuery = "select count(id) from " + tableName + " where id_musician = ? and id_song = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId());
            prepareStmt.setInt(2, song.getId());
        };         
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }

}
