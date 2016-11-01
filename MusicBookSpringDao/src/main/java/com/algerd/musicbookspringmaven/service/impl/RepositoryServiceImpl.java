
package com.algerd.musicbookspringmaven.service.impl;

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
import com.algerd.musicbookspringmaven.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositoryServiceImpl implements RepositoryService { 
    
    @Autowired
    private ArtistGenreRepository artistGenreRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumGenreRepository albumGenreRepository;
    @Autowired
    private ArtistRepository artistRepository;  
    @Autowired
    private ArtistReferenceRepository artistReferenceRepository;
    @Autowired
    private GenreRepository genreRepository; 
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MusicianAlbumRepository musicianAlbumRepository;
    @Autowired
    private MusicianGroupRepository musicianGroupRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    @Autowired
    private MusicianInstrumentRepository musicianInstrumentRepository;
    @Autowired
    private MusicianSongRepository musicianSongRepository; 
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private SongGenreRepository songGenreRepository;
    @Autowired
    private SongRepository songRepository;
                                    
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
