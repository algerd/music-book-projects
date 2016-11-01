
package db.entity;

/**
 * Обёртка объекта, которая содержит новое и старое значение объекта после его редактирования.
 */
public class WrapChangedEntity<T> {

    private final T oldEntity;
    private final T newEntity;
      
    public WrapChangedEntity(T oldEntity, T newEntity) {
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    public T getOld() {
        return oldEntity;
    }

    public T getNew() {
        return newEntity;
    }

}
