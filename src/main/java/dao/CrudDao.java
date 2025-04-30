package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    Optional<T> update(T entity);

}
