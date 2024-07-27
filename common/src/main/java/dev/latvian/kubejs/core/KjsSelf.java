package dev.latvian.kubejs.core;

/**
 *
 * @author ZZZank
 */
public interface KjsSelf<T> {

    default T kjs$self() {
        return (T) this;
    }
}
