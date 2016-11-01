
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.repository.ArtistReferenceRepository;
import java.sql.ResultSet;
import java.util.List;

public class ArtistReferenceRepositoryImpl extends CrudRepositoryImpl<ArtistReference> implements ArtistReferenceRepository {

    public ArtistReferenceRepositoryImpl() { 
        super("artist_reference");
    }
   
    @Override
    public List<ArtistReference> selectByArtist(Artist artist) {
        String prepareQuery = "select * from artist_reference where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        Extractor<ResultSet, Entity> extractor = resultSet -> fill(resultSet);
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
}
