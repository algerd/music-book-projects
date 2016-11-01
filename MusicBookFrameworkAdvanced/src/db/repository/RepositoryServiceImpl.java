
package db.repository;

import db.repository.AlbumGenreRepository;
import db.repository.AlbumRepository;
import db.repository.ArtistGenreRepository;
import db.repository.ArtistReferenceRepository;
import db.repository.ArtistRepository;
import db.repository.GenreRepository;
import db.repository.InstrumentRepository;
import db.repository.MusicianAlbumRepository;
import db.repository.MusicianGenreRepository;
import db.repository.MusicianGroupRepository;
import db.repository.MusicianInstrumentRepository;
import db.repository.MusicianRepository;
import db.repository.MusicianSongRepository;
import db.repository.SongGenreRepository;
import db.repository.SongRepository;
import db.repository.impl.AlbumGenreRepositoryImpl;
import db.repository.impl.AlbumRepositoryImpl;
import db.repository.impl.ArtistGenreRepositoryImpl;
import db.repository.impl.ArtistReferenceRepositoryImpl;
import db.repository.impl.ArtistRepositoryImpl;
import db.repository.impl.GenreRepositoryImpl;
import db.repository.impl.InstrumentRepositoryImpl;
import db.repository.impl.MusicianAlbumRepositoryImpl;
import db.repository.impl.MusicianGenreRepositoryImpl;
import db.repository.impl.MusicianGroupRepositoryImpl;
import db.repository.impl.MusicianInstrumentRepositoryImpl;
import db.repository.impl.MusicianSongRepositoryImpl;
import db.repository.impl.MusicianRepositoryImpl;
import db.repository.impl.SongGenreRepositoryImpl;
import db.repository.impl.SongRepositoryImpl;

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
