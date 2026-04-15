package nro.manager;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */

public interface IManager <E> {

    void add(E e);

    void remove(E e);

    E findByID(int id);
}
