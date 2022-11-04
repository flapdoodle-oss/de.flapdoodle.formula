package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.solver.GraphBuilder;
import de.flapdoodle.formula.solver.Solver;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import de.flapdoodle.formula.values.properties.Properties;
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToCalculateChangeableInstanceTest {

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
				updated = updated.change((ChangeableValue) id, result.get(id));
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

	private static Rules rulesFor(ImmutableCard card) {
		Rules current = Rules.empty();

		for (Item item : card.items()) {
			current = rulesFor(current, item);
		}

		return current
			.add(Calculate.value(Card.sumWithoutTax.withId(card.id()))
				.aggregating(card.items().stream()
					.map(item -> Item.sumProperty.withId(item.id()))
					.collect(Collectors.toList()))
				.by(list -> list.stream()
					.filter(Objects::nonNull)
					.mapToDouble(it -> it)
					.sum()))
			;
	}

	private static Rules rulesFor(Rules current, Item item) {
		return current
			.add(Calculate.value(Item.sumProperty.withId(item.id()))
				.using(Item.priceProperty.withId(item.id()), Item.quantityProperty.withId(item.id()))
				.by((price, count) -> (price!=null && count!=null) ? price * count : null));
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
		if (id.id().equals(card.id())) return ((ReadableValue<Card, T>) id).get(card);

		for (Item item : card.items()) {
			if (item.id().equals(id.id())) {
				return ((ReadableValue<Item, T>) id).get(item);
			}
		}

		throw new IllegalArgumentException("could not find "+id);
	}


	@Immutable
	public interface Card extends ChangeableInstance<Card> {
		CopyOnChangeProperty<Card, Double> sumWithoutTax= Properties.copyOnChange(Card.class, "sumWithoutTax", Card::sum, (item, value) -> ImmutableCard.copyOf(item).withSumWithoutTax(value));

		@Default
		default Id<Card> id() {
			return Id.idFor(Card.class);
		}

		List<Item> items();

		@Nullable Double sumWithoutTax();

		@Nullable Double tax();

		@Nullable Double sum();

		@Override default <T> Card change(ChangeableValue<?, T> id, T value) {
			if (id.id().equals(id())) {
				return ((ChangeableValue<Card, T>) id).change(this, value);
			}

			return ImmutableCard.copyOf(this)
				.withItems(items().stream()
					.map(item -> item.change(id, value))
					.collect(Collectors.toList()));
		}

		static ImmutableCard.Builder builder() {
			return ImmutableCard.builder();
		}
	}

	@Immutable
	public interface Item extends ChangeableInstance<Item> {
		CopyOnChangeProperty<Item, Double> sumProperty = Properties.copyOnChange(Item.class, "sum", Item::sum,
			(item, value) -> ImmutableItem.copyOf(item).withSum(value));
		ReadOnlyProperty<Item, Double> priceProperty = Properties.readOnly(Item.class, "price", Item::price);
		ReadOnlyProperty<Item, Integer> quantityProperty = Properties.readOnly(Item.class, "quantity", Item::quantity);

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

		static ImmutableItem.Builder builder() {
			return ImmutableItem.builder();
		}
	}
}
