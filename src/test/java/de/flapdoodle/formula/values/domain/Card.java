package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.calculate.Calculate;
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
public interface Card extends ChangeableInstance<Card>, HasRules {
	CopyOnChangeProperty<Card, Double> sumWithoutTax = copyOnChange(Card.class, "sumWithoutTax", Card::sum,
		(item, value) -> ImmutableCard.copyOf(item).withSumWithoutTax(value));

	@Value.Default
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
	@Value.Auxiliary
	default Rules addRulesTo(Rules current) {
		Related<Double, Id<Card>> min = de.flapdoodle.formula.Value.named("min", Double.class).relatedTo(id());
		Related<Double, Id<Card>> max = de.flapdoodle.formula.Value.named("max", Double.class).relatedTo(id());

		for (Item item : items()) {
			current = item.addRulesTo(current);
			current = current.add(
				Calculate.value(Item.isCheapestProperty.withId(item.id()))
					.using(min, Item.sumProperty.withId(item.id()))
					.by(Objects::equals)
			);
		}

		List<CopyOnChangeValue<Item, Double>> itemSumIds = items().stream()
			.map(item -> Item.sumProperty.withId(item.id()))
			.collect(Collectors.toList());

		return current
			.add(Calculate
				.value(Card.sumWithoutTax.withId(id()))
				.aggregating(itemSumIds)
				.by(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.sum()))
			.add(Calculate
				.value(min)
				.aggregating(itemSumIds)
				.by(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.min().orElse(0.0))
			)
			.add(Calculate
				.value(max)
				.aggregating(itemSumIds)
				.by(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.max().orElse(0.0))
			)
			;
	}

	static ImmutableCard.Builder builder() {
		return ImmutableCard.builder();
	}
}
