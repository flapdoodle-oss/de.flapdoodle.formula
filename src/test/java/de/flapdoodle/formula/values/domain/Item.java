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

import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.rules.Rules;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.reflection.TypeInfo;
import de.flapdoodle.types.Maybe;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import org.immutables.value.Value;

import javax.annotation.Nullable;

import static de.flapdoodle.formula.values.properties.Properties.copyOnChange;
import static de.flapdoodle.formula.values.properties.Properties.readOnly;

@Value.Immutable
public interface Item extends ChangeableInstance<Item>, HasRules {
	CopyOnChangeProperty<Item, Double> sumProperty = copyOnChange(Item.class, "sum", Item::sum, (item, value) -> ImmutableItem.copyOf(item).withSum(value));
	ReadOnlyProperty<Item, Double> priceProperty = readOnly(Item.class, "price", Item::price);
	ReadOnlyProperty<Item, Integer> quantityProperty = readOnly(Item.class, "quantity", Item::quantity);
	CopyOnChangeProperty<Item, Boolean> isCheapestProperty = copyOnChange(Item.class, "isCheapest", Item::isCheapest, (item, value) -> ImmutableItem.copyOf(item).withIsCheapest(value));

	@Value.Default
	@Override
	default Id<Item> id() {
		return Id.idFor(TypeInfo.of(Item.class));
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
	@Value.Auxiliary
	default Rules addRulesTo(Rules current) {
		return current
			.add(Calculate
				.value(Item.sumProperty.withId(id()))
				.using(Item.priceProperty.withId(id()), Item.quantityProperty.withId(id()))
				.ifAllSetBy((price, quantity) -> price * quantity,"price*quantity"));
	}

	static ImmutableItem.Builder builder() {
		return ImmutableItem.builder();
	}
}
