
package com.algerd.musicbookspringmaven.service;

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

public interface RepositoryService {
    
    ArtistRepository getArtistRepository();
    AlbumRepository getAlbumRepository();
    SongRepository getSongRepository();
    GenreRepository getGenreRepository();
    ArtistReferenceRepository getArtistReferenceRepository();
    ArtistGenreRepository getArtistGenreRepository();
    MusicianRepository getMusicianRepository();
    MusicianGroupRepository getMusicianGroupRepository();
    AlbumGenreRepository getAlbumGenreRepository();
    SongGenreRepository getSongGenreRepository();
    MusicianGenreRepository getMusicianGenreRepository();
    MusicianAlbumRepository getMusicianAlbumRepository();
    MusicianSongRepository getMusicianSongRepository();
    InstrumentRepository getInstrumentRepository();
    MusicianInstrumentRepository getMusicianInstrumentRepository();
}
