
package main;

import db.entity.Album;
import db.entity.Artist;
import db.entity.Genre;
import db.entity.Song;
import db.table.AlbumTable;
import db.table.ArtistReferenceTable;
import db.table.ArtistTable;
import db.table.GenreTable;
import db.table.MusicianGroupTable;
import db.table.MusicianTable;
import db.table.SongTable;
import java.util.Map;

public class Loader {
    
    private final ArtistTable artistTable;
    private final ArtistReferenceTable artistReferenceTable;
    private final AlbumTable albumTable;
    private final SongTable songTable;
    private final GenreTable genreTable;
    private final MusicianGroupTable musicianGroupTable;
    private final MusicianTable musicianTable;
       
    public Loader() {
        artistTable = new ArtistTable();
        artistReferenceTable = new ArtistReferenceTable();
        artistTable.setArtistReferenceTable(artistReferenceTable);
        albumTable = new AlbumTable();
        albumTable.setReferenceTable(artistTable);
        songTable = new SongTable();
        songTable.setReferenceTable(albumTable);
        genreTable = new GenreTable();
        genreTable.setArtistTable(artistTable);
        musicianGroupTable = new MusicianGroupTable();
        musicianTable = new MusicianTable();
        musicianTable.setMusicianGroupTable(musicianGroupTable);
        artistTable.setMusicianGroupTable(musicianGroupTable);
    }
    
    /*
    Использование карты для поиска объектов с требуемым индексом увеличивает скорость поиска более чем в 2 раза
    по сравнению с простым перебором.
    */
    public void load() {
        genreTable.select();
        
        Map<Integer, Artist> artistMap = artistTable.select();
        // помещаем жанры в артистов
        for (Artist artist : artistTable.getEntities()) {
            for (Genre genre : genreTable.getEntities()) {
                if (genre.getId() == artist.getIdGenre()) {
                    artist.setGenre(genre);
                }
            }
        }     
        
        Map<Integer, Album> albumMap = albumTable.select();       
        // помещаем альбомы в контейнеры-родители и передаём ссылки объктов-контейнеров Artist детям Album 
        for (Album album : albumTable.getEntities()) {
            int idArtist = album.getIdArtist();
            Artist artist = artistMap.get(idArtist);
            artist.getChildren().add(album);
            album.setParent(artist);              
        }
        
        Map<Integer, Song> songMap = songTable.select();
        // помещаем песни в контейнеры-родители и передаём ссылки объктов-контейнеров Album детям Song 
         for (Song song : songTable.getEntities()) {
            int idAlbum = song.getIdAlbum();
            Album album = albumMap.get(idAlbum);
            album.getChildren().add(song);
            song.setParent(album);              
        } 
        artistReferenceTable.select();
        musicianTable.select();
        musicianGroupTable.select();
    }
          
    public ArtistTable getArtistTable() {
        return artistTable;
    }

    public AlbumTable getAlbumTable() {
        return albumTable;
    }

    public SongTable getSongTable() {
        return songTable;
    }

    public GenreTable getGenreTable() {
        return genreTable;
    }

    public MusicianGroupTable getMusicianGroupTable() {
        return musicianGroupTable;
    }

    public MusicianTable getMusicianTable() {
        return musicianTable;
    }

    public ArtistReferenceTable getArtistReferenceTable() {
        return artistReferenceTable;
    }
                        
}
