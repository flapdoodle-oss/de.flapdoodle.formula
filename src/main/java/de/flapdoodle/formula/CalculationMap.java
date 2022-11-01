package de.flapdoodle.formula;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public final class CalculationMap {
	private final Map<Value<?>, Calculation<?>> map;

	private CalculationMap(Map<Value<?>, Calculation<?>> map) {
		this.map = map;
	}

	public Collection<Calculation<?>> values() {
		return map.values();
	}

	public Set<Value<?>> keys() {
		return map.keySet();
	}

	public <T> Calculation<T> get(Value<T> key) {
		return (Calculation<T>) map.get(key);
	}

	public boolean contains(Value<?> it) {
		return map.containsKey(it);
	}

	public CalculationMap add(Calculation<?> calculation) {
		Preconditions.checkArgument(!map.containsKey(calculation.destination()), "calculation already set for %s", calculation);

		return new CalculationMap(ImmutableMap.<Value<?>, Calculation<?>>builder()
			.putAll(map)
			.put(calculation.destination(), calculation)
			.build());
	}

	public CalculationMap addAll(List<Calculation<?>> calculations) {
		ImmutableMap<Value<?>, Calculation<?>> newCalculations = Maps.uniqueIndex(calculations, Calculation::destination);
		Sets.SetView<? extends Value<?>> duplicates = Sets.intersection(newCalculations.keySet(), map.keySet());

		Preconditions.checkArgument(duplicates.isEmpty(), "calculation already set for %s", duplicates);

		return new CalculationMap(ImmutableMap.<Value<?>, Calculation<?>>builder()
			.putAll(map)
			.putAll(newCalculations)
			.build());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalculationMap that = (CalculationMap) o;
		return map.equals(that.map);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	public static CalculationMap empty() {
		return new CalculationMap(ImmutableMap.of());
	}
}
