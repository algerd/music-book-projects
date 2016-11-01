
package mvc.artist.model;

import db.entity.Musician;
import db.entity.MusicianGroup;

public class MusicianWrapper {
    private Musician musician;
    private MusicianGroup musicianGroup;    

    public Musician getMusician() {
        return musician;
    }
    public void setMusician(Musician musician) {
        this.musician = musician;
    }

    public MusicianGroup getMusicianGroup() {
        return musicianGroup;
    }
    public void setMusicianGroup(MusicianGroup musicianGroup) {
        this.musicianGroup = musicianGroup;
    }
        
}
