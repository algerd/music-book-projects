
package com.algerd.musicbookspringmaven.spring.config;

import com.algerd.musicbookspringmaven.dbDriver.impl.DataSourceImpl;
import com.algerd.musicbookspringmaven.repository.AlbumGenreRepository;
import com.algerd.musicbookspringmaven.repository.AlbumRepository;
import com.algerd.musicbookspringmaven.repository.ArtistGenreRepository;
import com.algerd.musicbookspringmaven.repository.ArtistReferenceRepository;
import com.algerd.musicbookspringmaven.repository.ArtistRepository;
import com.algerd.musicbookspringmaven.repository.GenreRepository;
import com.algerd.musicbookspringmaven.repository.InstrumentRepository;
import com.algerd.musicbookspringmaven.repository.MusicianAlbumRepository;
import com.algerd.musicbookspringmaven.repository.MusicianGenreRepository;
import com.algerd.musicbookspringmaven.repository.MusicianGroupRepository;
import com.algerd.musicbookspringmaven.repository.MusicianInstrumentRepository;
import com.algerd.musicbookspringmaven.repository.MusicianRepository;
import com.algerd.musicbookspringmaven.repository.MusicianSongRepository;
import com.algerd.musicbookspringmaven.repository.SongGenreRepository;
import com.algerd.musicbookspringmaven.repository.SongRepository;
import com.algerd.musicbookspringmaven.repository.impl.AlbumRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.AlbumGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.ArtistGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.ArtistReferenceRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.ArtistRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.GenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.InstrumentRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianAlbumRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianGroupRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianInstrumentRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianSongRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.SongGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.SongRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import com.algerd.musicbookspringmaven.dbDriver.DataSource;

@Configuration
@PropertySource({"classpath:properties/dbconnect.properties"})
public class RepositoryConfig {
    
   @Autowired
   Environment env;
    
    @Bean DataSource connector() {
        DataSourceImpl dataSource = new DataSourceImpl();
        dataSource.setUrl(env.getProperty("url"));
        dataSource.setDriverClassName(env.getProperty("driver-class-name"));
        dataSource.setUser(env.getProperty("user"));
        dataSource.setPassword(env.getProperty("password"));
        dataSource.setDbProperties("properties/dbconfig.properties");      
        return dataSource;
    }
    
    @Bean
    public AlbumRepository albumRepository() {
        return new AlbumRepositoryImpl();
    }
    
    @Bean
    public AlbumGenreRepository albumGenreRepository() {
        return new AlbumGenreRepositoryImpl();
    }
    
    @Bean
    public ArtistGenreRepository artistGenreRepository() {
        return new ArtistGenreRepositoryImpl();
    }
    
    @Bean
    public ArtistReferenceRepository artistReferenceRepository() {
        return new ArtistReferenceRepositoryImpl();
    }
    
    @Bean
    public ArtistRepository artistRepository() {
        return new ArtistRepositoryImpl();
    }
        
    @Bean
    public GenreRepository genreRepository() {
        return new GenreRepositoryImpl();
    }
    
    @Bean
    public InstrumentRepository instrumentRepository() {
        return new InstrumentRepositoryImpl();
    }
    
    @Bean
    public MusicianAlbumRepository musicianAlbumRepository() {
        return new MusicianAlbumRepositoryImpl();
    } 
    
    @Bean
    public MusicianGroupRepository musicianGroupRepository() {
        return new MusicianGroupRepositoryImpl();
    }
    
    @Bean
    public MusicianGenreRepository musicianGenreRepository() {
        return new MusicianGenreRepositoryImpl();
    }
    
    @Bean
    public MusicianInstrumentRepository musicianInstrumentRepository() {
        return new MusicianInstrumentRepositoryImpl();
    }
    
    @Bean
    public MusicianSongRepository musicianSongRepository() {
        return new MusicianSongRepositoryImpl();
    }
    
    @Bean
    public MusicianRepository musicianRepository() {
        return new MusicianRepositoryImpl();
    }
 
    @Bean
    public SongGenreRepository songGenreRepository() {
        return new SongGenreRepositoryImpl();
    }

    @Bean
    public SongRepository songRepository() {
        return new SongRepositoryImpl();
    }

}
