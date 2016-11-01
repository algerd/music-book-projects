
package db.repository;

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
