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
package de.flapdoodle.formula.howto;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.solver.*;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.types.Maybe;
import de.flapdoodle.formula.values.matcher.ChangeableValue;
import de.flapdoodle.formula.values.matcher.IdMatcher;
import de.flapdoodle.formula.values.matcher.ReadableValue;
import de.flapdoodle.formula.values.properties.*;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToCalculateOnObjectTreeTest {

	@Test
	void sumOfItemsInCard() {
		ImmutableCard card = Card.builder()
			.addItems(Item.builder().name("box").quantity(2).price(10.5).build())
			.addItems(Item.builder().name("book").quantity(1).price(9.95).build())
			.addItems(Item.builder().name("nail").quantity(10).price(2.55).build())
			.build();

		Solver.Result result = Solver.solve(GraphBuilder.build(rulesFor(card)), valueLookup(card));

		Card updated = card;

		for (Value<?> id : result.validatedValues()) {
			if (id instanceof ChangeableValue) {
				updated = update(updated, (ChangeableValue) id, result.get(id));
			}
		}

		assertThat(updated.items().get(0).sum())
			.isEqualTo(2*10.5);
		assertThat(updated.items().get(1).sum())
			.isEqualTo(1*9.95);
		assertThat(updated.items().get(2).sum())
			.isEqualTo(10*2.55);

		assertThat(updated.sumWithoutTax())
			.isEqualTo(2*10.5+9.95+10*2.55);
	}
	private <T> Card update(Card updated, ChangeableValue<?,T> id, T value) {
		Maybe<? extends ChangeableValue<Card, T>> matchCard = id.matching(updated);
		if (matchCard.hasSome()) {
			return matchCard.map(c -> c.change(updated, value)).get();
		}

		List<Item> items = updated.items();

		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			Maybe<? extends ChangeableValue<Item, T>> matchItem = id.matching(item);
			if (matchItem.hasSome()) {
				return ImmutableCard.copyOf(updated)
					.withItems(ImmutableList.<Item>builder()
						.addAll(items.subList(0,i))
						.add(matchItem.map(m -> m.change(item, value)).get())
						.addAll(items.subList(i+1, items.size()))
						.build());
			}
		}

		return updated;
	}

	private Solver.ValueLookup valueLookup(Card card) {
		return new Solver.ValueLookup() {
			@Override
			public <T> @Nullable T get(de.flapdoodle.formula.Value<T> id) {
				if (id instanceof ReadableValue) {
					return valueOf(card, (ReadableValue<?, ? extends T>) id);
				}
				throw new IllegalArgumentException("not implemented");
			}
		};
	}

	private <T> T valueOf(Card card, ReadableValue<?, T> id) {
		Maybe<T> matchCard = id.matching(card)
			.map(m -> m.get(card));

		if (matchCard.hasSome()) return matchCard.get();

		for (Item item : card.items()) {
			Maybe<? extends ReadableValue<Item, T>> matchItem = id.matching(item);
			if (matchItem.hasSome()) {
				return matchItem.map(i -> i.get(item)).get();
			}
		}

		throw new IllegalArgumentException("could not find "+id);
	}

	private static Rules rulesFor(ImmutableCard card) {
		Rules current = Rules.empty();

		for (Item item : card.items()) {
			current = rulesFor(current, item);
		}

		return current
			.add(Calculate.value(Card.sumWithoutTax.matching(IdMatcher.matching(card, Card::id)))
				.aggregating(card.items().stream()
					.map(item -> Item.sumProperty.matching(IdMatcher.matching(item, Item::id)))
					.collect(Collectors.toList()))
				.by(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.sum()))
			;
	}
	private static Rules rulesFor(Rules current, Item item) {
		IdMatcher<Item> matchItem = IdMatcher.matching(item, Item::id);

		return current
			.add(Calculate.value(Item.sumProperty.matching(matchItem))
					.using(Item.priceProperty.matching(matchItem), Item.quantityProperty.matching(matchItem))
					.by((price, count) -> (price!=null && count!=null) ? price * count : null));
	}

	@Immutable
	static abstract class InstanceAttribute<O, T> implements Value<T>, ValueSource<T>, ValueSink<T> {
		@Parameter
		protected abstract Id<? super O> id();

		@Parameter
		protected abstract ModifiableProperty<O, T> property();

		public static <O extends HasId<? super O>, T> InstanceAttribute<O, T> of(O instance, ModifiableProperty<O, T> property) {
			return ImmutableInstanceAttribute.of(instance.id(), property);
		}
	}

	public interface HasId<T> {
		Id<T> id();
	}

	@Immutable
	public interface Card extends HasId<Card> {
		CopyOnChangeProperty<Card, Double> sumWithoutTax= Properties.copyOnChange(Card.class, "sumWithoutTax", Card::sum, (item, value) -> ImmutableCard.copyOf(item).withSumWithoutTax(value));

		@Default
		default Id<Card> id() {
			return Id.idFor(Card.class);
		}

		List<Item> items();

		@Nullable Double sumWithoutTax();

		@Nullable Double tax();

		@Nullable Double sum();

		static ImmutableCard.Builder builder() {
			return ImmutableCard.builder();
		}
	}

	@Immutable
	public interface Item extends HasId<Item> {
		CopyOnChangeProperty<Item, Double> sumProperty= Properties.copyOnChange(Item.class, "sum", Item::sum, (item, value) -> ImmutableItem.copyOf(item).withSum(value));
		ReadOnlyProperty<Item, Double> priceProperty = Properties.readOnly(Item.class, "price", Item::price);
		ReadOnlyProperty<Item, Integer> quantityProperty = Properties.readOnly(Item.class, "quantity", Item::quantity);

		@Default
		default Id<Item> id() {
			return Id.idFor(Item.class);
		}

		@Nullable String name();

		@Nullable Integer quantity();

		@Nullable Double price();

		@Nullable Double sum();

		@Nullable Boolean isCheapest();

		static ImmutableItem.Builder builder() {
			return ImmutableItem.builder();
		}
	}
}
