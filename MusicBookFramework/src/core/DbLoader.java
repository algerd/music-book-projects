
package core;

import db.table.AlbumGenreTable;
import db.table.AlbumTable;
import db.table.ArtistGenreTable;
import db.table.ArtistReferenceTable;
import db.table.ArtistTable;
import db.table.GenreTable;
import db.table.InstrumentTable;
import db.table.MusicianAlbumTable;
import db.table.MusicianGenreTable;
import db.table.MusicianGroupTable;
import db.table.MusicianInstrumentTable;
import db.table.MusicianSongTable;
import db.table.MusicianTable;
import db.table.SongGenreTable;
import db.table.SongTable;

public class DbLoader {  
    
    private final ArtistGenreTable artistGenreTable = new ArtistGenreTable();
    private final AlbumTable albumTable = new AlbumTable();
    private final AlbumGenreTable albumGenreTable = new AlbumGenreTable();
    private final ArtistTable artistTable = new ArtistTable();    
    private final ArtistReferenceTable artistReferenceTable = new ArtistReferenceTable();
    private final GenreTable genreTable = new GenreTable();  
    private final InstrumentTable instrumentTable = new InstrumentTable();
    private final MusicianAlbumTable musicianAlbumTable = new MusicianAlbumTable(); 
    private final MusicianGroupTable musicianGroupTable = new MusicianGroupTable(); 
    private final MusicianGenreTable musicianGenreTable = new MusicianGenreTable();
    private final MusicianInstrumentTable musicianInstrumentTable = new MusicianInstrumentTable();
    private final MusicianSongTable musicianSongTable = new MusicianSongTable(); 
    private final MusicianTable musicianTable = new MusicianTable();
    private final SongGenreTable songGenreTable = new SongGenreTable();
    private final SongTable songTable = new SongTable();
                                    
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

    public ArtistReferenceTable getArtistReferenceTable() {
        return artistReferenceTable;
    }  

    public ArtistGenreTable getArtistGenreTable() {
        return artistGenreTable;
    }

    public MusicianTable getMusicianTable() {
        return musicianTable;
    }

    public MusicianGroupTable getMusicianGroupTable() {
        return musicianGroupTable;
    } 

    public AlbumGenreTable getAlbumGenreTable() {
        return albumGenreTable;
    }

    public SongGenreTable getSongGenreTable() {
        return songGenreTable;
    }

    public MusicianGenreTable getMusicianGenreTable() {
        return musicianGenreTable;
    } 

    public MusicianAlbumTable getMusicianAlbumTable() {
        return musicianAlbumTable;
    }

    public MusicianSongTable getMusicianSongTable() {
        return musicianSongTable;
    }

    public InstrumentTable getInstrumentTable() {
        return instrumentTable;
    }

    public MusicianInstrumentTable getMusicianInstrumentTable() {
        return musicianInstrumentTable;
    }
                                   
}
