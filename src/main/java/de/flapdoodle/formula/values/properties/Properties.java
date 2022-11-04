package de.flapdoodle.formula.values.properties;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Properties {
	private Properties() {
		// no instance
	}

	public static <O, T> ReadOnlyProperty<O, T> readOnly(Class<O> type, String name, Function<O, T> getter) {
		return ReadOnlyProperty.of(type,name,getter);
	}

	public static <O, T> CopyOnChangeProperty<O, T> copyOnChange(Class<O> type, String name, Function<O, T> getter, BiFunction<O, T, O> copyOnWrite) {
		return CopyOnChangeProperty.of(type,name,getter,copyOnWrite);
	}
}
