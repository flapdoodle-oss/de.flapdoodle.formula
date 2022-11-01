package de.flapdoodle.formula;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public final class ValidationMap {
	private final Map<Value<?>, Validation<?>> map;

	private ValidationMap(Map<Value<?>, Validation<?>> map) {
		this.map = map;
	}

	public Collection<Validation<?>> values() {
		return map.values();
	}

	public Set<Value<?>> keys() {
		return map.keySet();
	}

	public <T> Validation<T> get(Value<T> key) {
		return (Validation<T>) map.get(key);
	}

	public boolean contains(Value<?> it) {
		return map.containsKey(it);
	}


	public ValidationMap add(Validation<?> validation) {
		Preconditions.checkArgument(!map.containsKey(validation.destination()), "validation already set for %s", validation);

		return new ValidationMap(ImmutableMap.<Value<?>, Validation<?>>builder()
			.putAll(map)
			.put(validation.destination(), validation)
			.build());
	}

	public ValidationMap addAll(List<Validation<?>> validations) {
		ImmutableMap<Value<?>, Validation<?>> newCalculations = Maps.uniqueIndex(validations, Validation::destination);
		Sets.SetView<? extends Value<?>> duplicates = Sets.intersection(newCalculations.keySet(), map.keySet());

		Preconditions.checkArgument(duplicates.isEmpty(), "validation already set for %s", duplicates);

		return new ValidationMap(ImmutableMap.<Value<?>, Validation<?>>builder()
			.putAll(map)
			.putAll(newCalculations)
			.build());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ValidationMap that = (ValidationMap) o;
		return map.equals(that.map);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	public static ValidationMap empty() {
		return new ValidationMap(ImmutableMap.of());
	}
}
