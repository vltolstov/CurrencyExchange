package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    void save(T entity);

    void update(T entity);

}
