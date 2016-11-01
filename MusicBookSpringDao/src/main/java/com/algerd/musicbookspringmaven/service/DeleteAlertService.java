
package com.algerd.musicbookspringmaven.service;

import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianAlbum;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import com.algerd.musicbookspringmaven.entity.MusicianSong;
import com.algerd.musicbookspringmaven.entity.Song;


public interface DeleteAlertService {
    
    void show(Album album);
    void show(Artist artist);
    void show(Genre genre);
    void show(Instrument instrument);
    void show(MusicianAlbum musicianAlbum);
    void show(Musician musician);
    void show(MusicianSong musicianSong);
    void show(Song song);
    void show(MusicianGroup musicianGroup);
    void show(ArtistReference artistReference);
    
}
