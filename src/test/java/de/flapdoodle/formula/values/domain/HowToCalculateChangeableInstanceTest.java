/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.solver.GraphBuilder;
import de.flapdoodle.formula.solver.Solver;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.types.Maybe;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import de.flapdoodle.formula.values.properties.Properties;
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.flapdoodle.formula.values.properties.Properties.copyOnChange;
import static de.flapdoodle.formula.values.properties.Properties.readOnly;
import static org.assertj.core.api.Assertions.assertThat;

public class HowToCalculateChangeableInstanceTest {

	@Test
	void sumOfItemsInCard() {
		Card card = Card.builder()
			.addItems(Item.builder().name("box").quantity(2).price(10.5).build())
			.addItems(Item.builder().name("book").quantity(1).price(9.95).build())
			.addItems(Item.builder().name("nail").quantity(10).price(2.55).build())
			.build();

		Solver.Result result = Solver.solve(
			GraphBuilder.build(card.addRules(Rules.empty())),
			valueLookup(card)
		);

		Card updated = card;

		for (Value<?> id : result.validatedValues()) {
			if (id instanceof ChangeableValue) {
				updated = updated.change((ChangeableValue) id, result.get(id));
			}
		}

		assertThat(updated.items().get(0).sum())
			.isEqualTo(2 * 10.5);
		assertThat(updated.items().get(1).sum())
			.isEqualTo(1 * 9.95);
		assertThat(updated.items().get(2).sum())
			.isEqualTo(10 * 2.55);

		assertThat(updated.sumWithoutTax())
			.isEqualTo(2 * 10.5 + 9.95 + 10 * 2.55);
	}

	private Solver.ValueLookup valueLookup(Card card) {
		return new Solver.ValueLookup() {
			@Override
			public <T> @Nullable T get(de.flapdoodle.formula.Value<T> id) {
				if (id instanceof ReadableValue) {
					return card.findValue((ReadableValue<?, ? extends T>) id)
						.getOrThrow(new IllegalArgumentException("not found: " + id));
				}
				throw new IllegalArgumentException("not implemented");
			}
		};
	}

	interface HasRules {
		@Auxiliary
		Rules addRules(Rules current);
	}

	@Immutable
	public interface Card extends ChangeableInstance<Card>, HasRules {
		CopyOnChangeProperty<Card, Double> sumWithoutTax = copyOnChange(Card.class, "sumWithoutTax", Card::sum,
			(item, value) -> ImmutableCard.copyOf(item).withSumWithoutTax(value));

		@Default
		default Id<Card> id() {
			return Id.idFor(Card.class);
		}

		List<Item> items();

		@Nullable Double sumWithoutTax();

		@Nullable Double tax();

		@Nullable Double sum();

		@Override
		default <T> Card change(ChangeableValue<?, T> id, T value) {
			if (id.id().equals(id())) {
				return ((ChangeableValue<Card, T>) id).change(this, value);
			}

			return ImmutableCard.copyOf(this)
				.withItems(items().stream()
					.map(item -> item.change(id, value))
					.collect(Collectors.toList()));
		}

		@Override
		default <T> Maybe<T> findValue(ReadableValue<?, T> id) {
			if (id.id().equals(id())) {
				return Maybe.some(((ReadableValue<Card, T>) id).get(this));
			}

			for (Item item : items()) {
				Maybe<T> result = item.findValue(id);
				if (result.hasSome()) return result;
			}

			return Maybe.none();
		}

		@Override
		@Auxiliary
		default Rules addRules(Rules current) {
			for (Item item : items()) {
				current = item.addRules(current);
			}

			return current
				.add(Calculate
					.value(Card.sumWithoutTax.withId(id()))
					.aggregating(items().stream()
						.map(item -> Item.sumProperty.withId(item.id()))
						.collect(Collectors.toList()))
					.by(list -> list.stream()
						.filter(Objects::nonNull)
						.mapToDouble(it -> it)
						.sum()))
				;
		}

		static ImmutableCard.Builder builder() {
			return ImmutableCard.builder();
		}
	}

	@Immutable
	public interface Item extends ChangeableInstance<Item>, HasRules {
		CopyOnChangeProperty<Item, Double> sumProperty = copyOnChange(Item.class, "sum", Item::sum, (item, value) -> ImmutableItem.copyOf(item).withSum(value));
		ReadOnlyProperty<Item, Double> priceProperty = readOnly(Item.class, "price", Item::price);
		ReadOnlyProperty<Item, Integer> quantityProperty = readOnly(Item.class, "quantity", Item::quantity);

		@Default
		@Override
		default Id<Item> id() {
			return Id.idFor(Item.class);
		}

		@Nullable String name();

		@Nullable Integer quantity();

		@Nullable Double price();

		@Nullable Double sum();

		@Nullable Boolean isCheapest();

		@Override
		default <T> Item change(ChangeableValue<?, T> id, T value) {
			if (id.id().equals(id())) {
				return ((ChangeableValue<Item, T>) id).change(this, value);
			}
			return this;
		}

		@Override
		default <T> Maybe<T> findValue(ReadableValue<?, T> id) {
			if (id.id().equals(id())) {
				return Maybe.some(((ReadableValue<Item, T>) id).get(this));
			}
			return Maybe.none();
		}

		@Override
		@Auxiliary
		default Rules addRules(Rules current) {
			return current
				.add(Calculate
					.value(Item.sumProperty.withId(id()))
					.using(Item.priceProperty.withId(id()), Item.quantityProperty.withId(id()))
					.by((price, count) -> (price != null && count != null) ? price * count : null));
		}

		static ImmutableItem.Builder builder() {
			return ImmutableItem.builder();
		}
	}
}
