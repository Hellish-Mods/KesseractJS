package dev.latvian.kubejs.util;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier, -1L);
	}

	public static <T> Lazy<T> of(Supplier<T> supplier, long expiresInMs) {
		return new Lazy<>(supplier, System.currentTimeMillis() + expiresInMs);
	}

	public static <T> Lazy<T> serviceLoader(Class<T> type) {
        return of(() -> {
            var loaded = ServiceLoader.load(type).iterator();
            if (loaded.hasNext()) {
                return loaded.next();
            }
            throw new RuntimeException(String.format(
                "Could not find platform implementation for %s!",
                type.getSimpleName()
            ));
        });
	}

	private final Supplier<T> factory;
	private T value;
	private boolean cached;
	private final long expires;

	private Lazy(Supplier<T> factory, long expires) {
		this.factory = factory;
		this.expires = expires;
	}

	@Override
	public T get() {
		if (expires >= 0L && System.currentTimeMillis() > expires) {
			cached = false;
		} else if (cached) {
			return value;
		}

		value = factory.get();
		cached = true;
		return value;
	}

	public void forget() {
		value = null;
		cached = false;
	}
}
