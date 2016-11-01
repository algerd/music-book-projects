
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.repository.GenreRepository;
import com.algerd.musicbookspringmaven.entity.Genre;

public class GenreRepositoryImpl extends CrudRepositoryImpl<Genre> implements GenreRepository {
       
    public GenreRepositoryImpl() { 
        super("genre");
    }  
   
}
