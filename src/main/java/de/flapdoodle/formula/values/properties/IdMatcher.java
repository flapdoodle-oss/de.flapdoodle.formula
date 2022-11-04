package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.function.Function;

@Value.Immutable
public abstract class IdMatcher<O> implements Matcher<O> {
	@Value.Parameter
	protected abstract Id<O> id();
	@Value.Parameter
	@Value.Auxiliary
	protected abstract Function<O, Id<O>> idExtractor();

	@Override
	public String toString() {
		return getClass().getSimpleName()+"{"+id()+"}";
	}

	@Override
	public boolean match(Object maybeInstance) {
		return id().asInstance(maybeInstance)
			.map(it -> id().equals(idExtractor().apply(it)))
			.orElse(false);
	}

	public static <O> IdMatcher<O> matching(O instance, Function<O, Id<O>> idExtractor) {
		return ImmutableIdMatcher.of(idExtractor.apply(instance), idExtractor);
	}
}
