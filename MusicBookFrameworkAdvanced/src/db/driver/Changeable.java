
package db.driver;

public interface Changeable<T> {
    
    T getOld();
    T getNew();
}
