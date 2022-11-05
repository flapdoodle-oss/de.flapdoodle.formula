package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.solver.Solver;

import javax.annotation.Nullable;

public class CardValueLookup implements Solver.ValueLookup {
	private final Card card;

	public CardValueLookup(Card card) {
		this.card = card;
	}
	@Override
	public <T> @Nullable T get(Value<T> id) {
		if (id instanceof ReadableValue) {
			return card.findValue((ReadableValue<?, ? extends T>) id)
				.getOrThrow(new IllegalArgumentException("not found: " + id));
		}
		throw new IllegalArgumentException("not implemented");
	}
}
