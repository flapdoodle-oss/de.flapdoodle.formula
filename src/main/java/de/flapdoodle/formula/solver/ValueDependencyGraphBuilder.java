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
package de.flapdoodle.formula.solver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.rules.CalculationMap;
import de.flapdoodle.formula.rules.Rules;
import de.flapdoodle.formula.rules.ValidationMap;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.graph.GraphBuilder;
import de.flapdoodle.graph.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ValueDependencyGraphBuilder {

	private ValueDependencyGraphBuilder() {
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
			calculations.all().stream()
				.flatMap(it -> it.sources().stream()),
			validations.all().stream()
				.flatMap(it -> it.sources().stream())
		).collect(ImmutableSet.toImmutableSet());

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

		calculations.all().forEach(calculation -> builder.add(calculation.destination(), calculation, validations.get(calculation.destination())));
		validations.all().forEach(validation -> {
			if (!calculations.contains(validation.destination())) {
				builder.add(validation.destination(), null, validation);
			}
		});

		return builder.build();
	}

	private static class Wrapper {
		private final GraphBuilder<Value<?>, DefaultEdge, DefaultDirectedGraph<Value<?>, DefaultEdge>> builder = GraphBuilder.withDirectedGraph();

		Wrapper add(
			Value<?> destination,
			@Nullable
			Calculation<?> calculation,
			@Nullable
			Validation<?> validation
		) {
			Set<? extends ValueSource<?>> calcSources = Optional.ofNullable(calculation)
				.filter(c -> {
					c.sources().forEach(it -> {
						Preconditions.checkArgument(!(it instanceof Unvalidated), "not allowed in calculation: %s", it);
					});
					return true;
				})
				.map(Calculation::sources)
				.orElse(ImmutableSet.of());

			Set<? extends ValueSource<?>> validationSources = Optional.ofNullable(validation)
				.map(Validation::sources)
				.orElse(ImmutableSet.of());

			ImmutableSet<Value<?>> allSources = ImmutableSet.<Value<?>>builder()
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
