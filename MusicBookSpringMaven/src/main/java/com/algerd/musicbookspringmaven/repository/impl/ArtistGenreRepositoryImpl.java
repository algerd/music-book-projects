
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.ArtistGenreRepository;
import java.sql.ResultSet;
import java.util.List;

public class ArtistGenreRepositoryImpl extends CrudRepositoryImpl<ArtistGenre> implements ArtistGenreRepository {
   
    @Override
    public List<ArtistGenre> selectJoinByArtist(Artist artist) {   
        String prepareQuery = 
            "select "
                + "artist_genre.id, "
                + "artist_genre.id_genre, "
                + "artist_genre.id_artist, "              
                + "genre.name "
            + "from artist_genre "
                + "left join genre on artist_genre.id_genre = genre.id "
            + "where artist_genre.id_artist = ?";  
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, artist.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            ArtistGenre artistGenre = fill(resultSet);
            Genre genre = new Genre();
            genre.setId(artistGenre.getId_genre());
            genre.setName(resultSet.getString("name"));
            artistGenre.setGenre(genre);
            return artistGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);       
    }
    
    @Override
    public List<ArtistGenre> selectJoinByGenre(Genre genre) {
        String prepareQuery = 
            "select "
                + "artist_genre.id, "
                + "artist_genre.id_genre, "
                + "artist_genre.id_artist, "             
                + "artist.name, "
                + "artist.rating "
            + "from artist_genre "
                + "left join artist on artist_genre.id_artist = artist.id "
            + "where artist_genre.id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };    
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            ArtistGenre artistGenre = fill(resultSet);
            Artist artist = new Artist();
            artist.setId(artistGenre.getId_artist());
            artist.setName(resultSet.getString("name"));
            artist.setRating(resultSet.getInt("rating"));
            artistGenre.setArtist(artist);
            return artistGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    };
    
    @Override
    public void deleteByArtist(Artist artist) {
        String prepareQuery = "delete from " + getTableName() + " where id_artist = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    @Override
    public int countArtistsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from artist_genre "
                + "where id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };       
        return super.fetchIntRow(prepareQuery, prepareSetter);
    }
    
}
