
package db.repository.impl;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Artist;
import db.driver.Entity;
import db.entity.Musician;
import db.entity.MusicianGroup;
import db.repository.MusicianGroupRepository;
import java.sql.ResultSet;
import java.util.List;

public class MusicianGroupRepositoryImpl extends CrudRepositoryImpl<MusicianGroup> implements MusicianGroupRepository {
    
    public MusicianGroupRepositoryImpl() { 
        super("musician_group");
    }
       
    @Override
    public List<MusicianGroup> selectJoinByMusician(Musician musician) {
        String prepareQuery = 
            "select "
                + "musician_group.id, "
                + "musician_group.id_musician, "
                + "musician_group.id_artist, "
                + "musician_group.start_date, "
                + "musician_group.end_date, "
                + "artist.name, "
                + "artist.rating "
            + "from musician_group "
                + "left join artist on musician_group.id_artist = artist.id "
            + "where musician_group.id_musician = ?"; 
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianGroup musicianGroup = fill(resultSet);
            Artist artist = new Artist();
            artist.setId(musicianGroup.getId_artist());
            artist.setName(resultSet.getString(6));
            artist.setRating(resultSet.getInt(7));
            musicianGroup.setArtist(artist);
            return musicianGroup;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
    @Override
    public List<MusicianGroup> selectJoinByArtist(Artist artist) {
        String prepareQuery = 
            "select "
                + "musician_group.id, "
                + "musician_group.id_musician, "
                + "musician_group.id_artist, "
                + "musician_group.start_date, "
                + "musician_group.end_date, "
                + "musician.name, "
                + "musician.rating "
            + "from musician_group "
                + "left join musician on musician_group.id_musician = musician.id "
            + "where musician_group.id_artist = ?";  
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, artist.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianGroup musicianGroup = fill(resultSet);
            Musician musician = new Musician();
            musician.setId(musicianGroup.getId_musician());
            musician.setName(resultSet.getString(6));
            musician.setRating(resultSet.getInt(7));
            musicianGroup.setMusician(musician);
            return musicianGroup;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
    @Override
    public boolean containsMusicianArtist(Musician musician, Artist artist) {
        String prepareQuery = "select count(id) from " + tableName + " where id_musician = ? and id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId());
            prepareStmt.setInt(2, artist.getId());
        };         
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
  
}
