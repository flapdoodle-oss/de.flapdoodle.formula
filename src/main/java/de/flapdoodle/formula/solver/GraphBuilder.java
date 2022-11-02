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
import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.calculate.CalculationMap;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.formula.validation.ValidationMap;
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
