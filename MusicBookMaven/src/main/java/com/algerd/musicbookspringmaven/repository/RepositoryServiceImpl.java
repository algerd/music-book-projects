
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.repository.impl.AlbumGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.AlbumRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.ArtistGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.ArtistReferenceRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.ArtistRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.GenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.InstrumentRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianAlbumRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianGroupRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianInstrumentRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianSongRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.MusicianRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.SongGenreRepositoryImpl;
import com.algerd.musicbookspringmaven.repository.impl.SongRepositoryImpl;

//@Service
public class RepositoryServiceImpl implements RepositoryService {  
    //@Inject - все репозитарии
    private final ArtistGenreRepository artistGenreRepository = new ArtistGenreRepositoryImpl();
    private final AlbumRepository albumRepository = new AlbumRepositoryImpl();
    private final AlbumGenreRepository albumGenreRepository = new AlbumGenreRepositoryImpl();
    private final ArtistRepository artistRepository = new ArtistRepositoryImpl();    
    private final ArtistReferenceRepository artistReferenceRepository = new ArtistReferenceRepositoryImpl();
    private final GenreRepository genreRepository = new GenreRepositoryImpl();  
    private final InstrumentRepository instrumentRepository = new InstrumentRepositoryImpl();
    private final MusicianAlbumRepository musicianAlbumRepository = new MusicianAlbumRepositoryImpl(); 
    private final MusicianGroupRepository musicianGroupRepository = new MusicianGroupRepositoryImpl(); 
    private final MusicianGenreRepository musicianGenreRepository = new MusicianGenreRepositoryImpl();
    private final MusicianInstrumentRepository musicianInstrumentRepository = new MusicianInstrumentRepositoryImpl();
    private final MusicianSongRepository musicianSongRepository = new MusicianSongRepositoryImpl(); 
    private final MusicianRepository musicianRepository = new MusicianRepositoryImpl();
    private final SongGenreRepository songGenreRepository = new SongGenreRepositoryImpl();
    private final SongRepository songRepository = new SongRepositoryImpl();
                                    
    @Override
    public ArtistRepository getArtistRepository() {
        return artistRepository;
    }

    @Override
    public AlbumRepository getAlbumRepository() {
        return albumRepository;
    } 

    @Override
    public SongRepository getSongRepository() {
        return songRepository;
    }

    @Override
    public GenreRepository getGenreRepository() {
        return genreRepository;
    }

    @Override
    public ArtistReferenceRepository getArtistReferenceRepository() {
        return artistReferenceRepository;
    }  

    @Override
    public ArtistGenreRepository getArtistGenreRepository() {
        return artistGenreRepository;
    }

    @Override
    public MusicianRepository getMusicianRepository() {
        return musicianRepository;
    }

    @Override
    public MusicianGroupRepository getMusicianGroupRepository() {
        return musicianGroupRepository;
    } 

    @Override
    public AlbumGenreRepository getAlbumGenreRepository() {
        return albumGenreRepository;
    }

    @Override
    public SongGenreRepository getSongGenreRepository() {
        return songGenreRepository;
    }

    @Override
    public MusicianGenreRepository getMusicianGenreRepository() {
        return musicianGenreRepository;
    } 

    @Override
    public MusicianAlbumRepository getMusicianAlbumRepository() {
        return musicianAlbumRepository;
    }

    @Override
    public MusicianSongRepository getMusicianSongRepository() {
        return musicianSongRepository;
    }

    @Override
    public InstrumentRepository getInstrumentRepository() {
        return instrumentRepository;
    }

    @Override
    public MusicianInstrumentRepository getMusicianInstrumentRepository() {
        return musicianInstrumentRepository;
    }
                                   
}
