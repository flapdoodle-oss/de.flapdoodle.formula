package de.flapdoodle.formula.types;

import com.google.common.collect.Maps;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class Id<O> {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract int count();

	@Value.Auxiliary
	public Optional<O> asInstance(Object value) {
		return type().isInstance(value)
			? Optional.of((O) value)
			: Optional.empty();
	}

	@Override public String toString() {
		return getClass().getSimpleName()+"{type="+type().getSimpleName()+", count="+count()+"}";
	}
	private static Map<Class<?>, Integer> typeCounterMap = Maps.newConcurrentMap();

	public static <O> Id<O> idFor(Class<O> type) {
		return ImmutableId.of(type, typeCounterMap.compute(type, (key, value) -> value != null ? value + 1 : 0));
	}
}