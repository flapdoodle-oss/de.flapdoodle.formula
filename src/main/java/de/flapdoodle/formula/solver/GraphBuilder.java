package de.flapdoodle.formula.solver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.flapdoodle.formula.*;
import de.flapdoodle.graph.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GraphBuilder {

	private GraphBuilder() {
		// no instance
	}

	public static ValueGraph build(Rules rules) {
		DefaultDirectedGraph<Value<?>, DefaultEdge> graph = buildGraph(rules.calculations(), rules.validations());

		return new ValueGraph(
			graph,
			rules.calculations(),
			rules.validations()
		);
	}

	private static DefaultDirectedGraph<Value<?>, DefaultEdge> buildGraph(CalculationMap calculations, ValidationMap validations) {
		Wrapper builder = new Wrapper();

		Set<ValueSource<?>> allSources = Stream.concat(
			calculations.values().stream()
				.flatMap(it -> it.sources().stream()),
			validations.values().stream()
				.flatMap(it -> it.sources().stream())
		).collect(Collectors.toSet());

		ImmutableSet<Value<?>> validatedOrCalculated = ImmutableSet.<Value<?>>builder()
			.addAll(calculations.keys())
			.addAll(validations.keys())
			.build();

		Sets.SetView<ValueSource<?>> unvalidatedOrCalculated = Sets.difference(allSources, validatedOrCalculated);

		unvalidatedOrCalculated.forEach(it -> {
			if (!(it instanceof Unvalidated)) {
				builder.add(it, null, null);
			}
		});

		calculations.values().forEach(calculation -> builder.add(calculation.destination(), calculation, validations.get(calculation.destination())));
		validations.values().forEach(validation -> {
			if (!calculations.contains(validation.destination())) {
				builder.add(validation.destination(), null, validation);
			}
		});

		return builder.build();
	}

	private static class Wrapper {
		private final Graphs.GraphBuilder<Value<?>, DefaultEdge, DefaultDirectedGraph<Value<?>, DefaultEdge>> builder = Graphs.graphBuilder(
			Graphs.<Value<?>>directedGraph()).get();

		Wrapper add(
			Value<?> destination,
			@Nullable
			Calculation<?> calculation,
			@Nullable
			Validation<?> validation
		) {
			List<Value<?>> calcSources = Optional.ofNullable(calculation)
				.map(c -> c.sources().stream().map(
					it -> {
						Preconditions.checkArgument(!(it instanceof Unvalidated), "not allowed in calculation: %s", it);
						return it;
					}
				).collect(Collectors.<Value<?>>toList()))
				.orElse(ImmutableList.of());

			List<? extends Value<?>> validationSources = Optional.ofNullable(validation)
				.map(Validation::sources)
				.orElse(ImmutableList.of());

			ImmutableList<Value<?>> allSources = ImmutableList.<Value<?>>builder()
				.addAll(calcSources)
				.addAll(validationSources)
				.build();

			builder.addVertex(destination);
			allSources.forEach(it -> {
				builder.addVertex(it);
				builder.addEdge(it, destination);
			});

			return this;
		}

		public DefaultDirectedGraph<Value<?>, DefaultEdge> build() {
			return builder.build();
		}
	}
}
