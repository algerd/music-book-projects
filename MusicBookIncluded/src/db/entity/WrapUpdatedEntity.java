
package db.entity;

/**
 * Обёртка объекта, которая содержит новое и старое значение объекта после его редактирования.
 */
public class WrapUpdatedEntity<T> {

    private final T oldEntity;
    private final T newEntity;
      
    public WrapUpdatedEntity(T oldEntity, T newEntity) {
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    public T getOldEntity() {
        return oldEntity;
    }

    public T getNewEntity() {
        return newEntity;
    }

}
