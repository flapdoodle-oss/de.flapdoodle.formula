package de.flapdoodle.formula.types;

import com.google.common.collect.Maps;

import java.util.Map;

public class TypeCounter {
	private final Map<Class<?>, Integer> typeCounterMap = Maps.newConcurrentMap();

	public int count(Class<?> type) {
		return typeCounterMap.compute(type, (key, value) -> value != null ? value + 1 : 0);
	}
}
