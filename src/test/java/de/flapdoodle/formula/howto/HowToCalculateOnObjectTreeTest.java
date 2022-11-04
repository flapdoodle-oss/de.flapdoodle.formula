package de.flapdoodle.formula.howto;

import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.solver.*;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.properties.*;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HowToCalculateOnObjectTreeTest {

	@Test
	void testMe() {
		ImmutableCard card = Card.builder()
			.addItems(Item.builder().name("box").quantity(2).price(10.5).build())
			.addItems(Item.builder().name("book").quantity(1).price(9.95).build())
			.addItems(Item.builder().name("nail").quantity(10).price(2.55).build())
			.build();

		System.out.println("--------------------");
		System.out.println(card);
		System.out.println("--------------------");

		ValueGraph valueGraph = GraphBuilder.build(rulesFor(card));

		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph());
		System.out.println("------------------");
		System.out.println(dot);
		System.out.println("------------------");

		Context result = Solver.solve(valueGraph, valueLookup(card));

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
		if (id.match(card).isPresent()) {
			ReadableValue<Card, T> matchingId = (ReadableValue<Card, T>) id;
			return matchingId.get(card);
		} else {
			for (Item item : card.items()) {
				if (id.match(item).isPresent()) {
					ReadableValue<Item, T> matchingId = (ReadableValue<Item, T>) id;
					return matchingId.get(item);
				}
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
					.by((price, count) -> 0.0));
	}

	@Immutable
	static abstract class InstanceAttribute<O, T> implements Value<T>, ValueSource<T>, ValueSink<T> {
		@Parameter
		protected abstract Id<? super O> id();

		@Parameter
		protected abstract ChangableProperty<O, T> property();

		public static <O extends HasId<? super O>, T> InstanceAttribute<O, T> of(O instance, ChangableProperty<O, T> property) {
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
