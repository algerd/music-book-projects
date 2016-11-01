
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.repository.ArtistReferenceRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class ArtistReferenceRepositoryImpl extends CrudRepositoryImpl<ArtistReference> implements ArtistReferenceRepository {
   
    @Override
    public List<ArtistReference> selectByArtist(Artist artist) {
        String prepareQuery = "select * from artist_reference where id_artist = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {artist.getId()}), 
            (ResultSet rs, int rowNum) -> getEntity(rs)   
        );
    }
    
}
