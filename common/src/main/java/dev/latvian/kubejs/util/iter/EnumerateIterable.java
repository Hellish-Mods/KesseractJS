package dev.latvian.kubejs.util.iter;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class EnumerateIterable<T> implements Iterable<EnumerateEntry<T>> {
    private final Iterable<T> inner;

    public EnumerateIterable(Iterable<T> inner) {
        this.inner = Objects.requireNonNull(inner);
    }

    @Override
    public @NotNull Iterator<EnumerateEntry<T>> iterator() {
        return new EnumerateIterator<>(inner.iterator());
    }
}
