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
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.calculate.Calculations;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.types.Maybe;
import de.flapdoodle.formula.values.Related;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.flapdoodle.formula.values.properties.Properties.copyOnChange;

@Value.Immutable
public interface Cart extends ChangeableInstance<Cart>, HasRules {
	CopyOnChangeProperty<Cart, Double> sumWithoutTax = copyOnChange(Cart.class, "sumWithoutTax", Cart::sum,
		(item, value) -> ImmutableCart.copyOf(item).withSumWithoutTax(value));

	@Value.Default
	default Id<Cart> id() {
		return Id.idFor(Cart.class);
	}

	List<Item> items();

	@Nullable Double sumWithoutTax();

	@Nullable Double tax();

	@Nullable Double sum();

	@Override
	default <T> Cart change(ChangeableValue<?, T> id, T value) {
		if (id.id().equals(id())) {
			return ((ChangeableValue<Cart, T>) id).change(this, value);
		}

		return ImmutableCart.copyOf(this)
			.withItems(items().stream()
				.map(item -> item.change(id, value))
				.collect(Collectors.toList()));
	}

	@Override
	default <T> Maybe<T> findValue(ReadableValue<?, T> id) {
		if (id.id().equals(id())) {
			return Maybe.some(((ReadableValue<Cart, T>) id).get(this));
		}

		for (Item item : items()) {
			Maybe<T> result = item.findValue(id);
			if (result.hasSome()) return result;
		}

		return Maybe.none();
	}

	@Override
	@Value.Auxiliary
	default Rules addRulesTo(Rules current) {
		Related<Double, Id<Cart>> min = de.flapdoodle.formula.Value.named("min", Double.class).relatedTo(id());
		Related<Double, Id<Cart>> max = de.flapdoodle.formula.Value.named("max", Double.class).relatedTo(id());

		for (Item item : items()) {
			current = item.addRulesTo(current);
			current = current.add(
				Calculate.value(Item.isCheapestProperty.withId(item.id()))
					.using(min, Item.sumProperty.withId(item.id()))
					.by(Calculations.explained(Objects::equals,"min==sum"))
			);
		}

		List<CopyOnChangeValue<Item, Double>> itemSumIds = items().stream()
			.map(item -> Item.sumProperty.withId(item.id()))
			.collect(Collectors.toList());

		return current
			.add(Calculate
				.value(Cart.sumWithoutTax.withId(id()))
				.aggregating(itemSumIds)
				.by(Calculations.explained(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.sum(),"sum(...)")))
			.add(Calculate
				.value(min)
				.aggregating(itemSumIds)
				.by(Calculations.explained(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.min().orElse(0.0),"min"))
			)
			.add(Calculate
				.value(max)
				.aggregating(itemSumIds)
				.by(Calculations.explained(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.max().orElse(0.0),"max"))
			)
			;
	}

	static ImmutableCart.Builder builder() {
		return ImmutableCart.builder();
	}
}
