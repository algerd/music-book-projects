
package db.repository.impl;

import db.repository.GenreRepository;
import db.entity.Genre;

public class GenreRepositoryImpl extends CrudRepositoryImpl<Genre> implements GenreRepository {
       
    public GenreRepositoryImpl() { 
        super("genre");
    }  
   
}
