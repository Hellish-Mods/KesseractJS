package dev.latvian.kubejs.util.iter;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class EnumerateIterator<T> implements Iterator<EnumerateEntry<T>> {
    private final Iterator<T> inner;
    private int index;

    public EnumerateIterator(Iterator<T> inner) {
        this.inner = Objects.requireNonNull(inner);
    }

    @Override
    public boolean hasNext() {
        return inner.hasNext();
    }

    @Override
    public EnumerateEntry<T> next() {
        return new EnumerateEntry<>(index++, inner.next());
    }

    @Override
    public void remove() {
        inner.remove();
    }
}
