package dev.latvian.kubejs.registry;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record BuilderType<T>(
		String type,
		Class<? extends BuilderBase<? extends T>> builderClass,
		BuilderFactory factory
) {
}