
package com.algerd.musicbookspringmaven.dbDriver;

public interface Changeable<T> {
    
    T getOld();
    T getNew();
}
