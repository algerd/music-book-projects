
package db.repository.impl;

import db.driver.PrepareSetter;
import db.driver.Extractor;
import db.entity.Album;
import db.entity.Artist;
import db.driver.Entity;
import db.entity.Musician;
import db.entity.MusicianAlbum;
import db.repository.MusicianAlbumRepository;
import java.sql.ResultSet;
import java.util.List;

public class MusicianAlbumRepositoryImpl extends CrudRepositoryImpl<MusicianAlbum> implements MusicianAlbumRepository {
    
    public MusicianAlbumRepositoryImpl() { 
        super("musician_album");
    }
   
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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianAlbum musicianAlbum = fill(resultSet);
            
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
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, album.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianAlbum musicianAlbum = fill(resultSet);
            Musician musician = new Musician();
            musician.setId(musicianAlbum.getId_musician());
            musician.setName(resultSet.getString("name"));
            musician.setRating(resultSet.getInt("rating"));
            musicianAlbum.setMusician(musician);
            return musicianAlbum;
        };       
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
    @Override
    public boolean containsMusicianAlbum(Musician musician, Album album) {
        String prepareQuery = "select count(id) from " + tableName + " where id_musician = ? and id_album = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId());
            prepareStmt.setInt(2, album.getId());
        };         
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
    
}
