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
package de.flapdoodle.formula.explain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.calculate.CalculationMap;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.formula.validation.ValidationMap;
import de.flapdoodle.graph.GraphAsDot;
import de.flapdoodle.graph.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class RuleDependencyGraph {
	private RuleDependencyGraph() {
		// no instance
	}

	public static String explain(Rules rules) {
		return renderGraphAsDot(build(rules));
	}

	private static DefaultDirectedGraph<RuleVertex, DefaultEdge> build(Rules rules) {
		return buildGraph(rules.calculations(), rules.validations());
	}

	public static String renderGraphAsDot(DefaultDirectedGraph<RuleVertex, DefaultEdge> graph) {
		return renderGraphAsDot(graph, HasHumanReadableLabel::asHumanReadable);
	}

	private static String renderGraphAsDot(DefaultDirectedGraph<RuleVertex, DefaultEdge> graph, Function<RuleVertex, String> labelOfValue) {
		IdGenerator idGenerator=new IdGenerator();
		return GraphAsDot.builder(idGenerator::idOf)
			.nodeAttributes(it -> ImmutableMap.<String, String>builder()
				.putAll(colorOf(it))
				.put("label", labelOfValue.apply(it))
				.build())
			.label("rules")
			.build()
			.asDot(graph);
	}

	private static Map<String, String> colorOf(RuleVertex node)  {
		return ImmutableMap.<String, String>builder()
			.put("fillcolor",(node instanceof RuleVertex.ValueVertex) ? "gray81" : "lightskyblue")
			.put("style","filled")
			.put("shape","rectangle")
			.build();
	}

	private static class IdGenerator {
		private final Map<RuleVertex, String> idMap=new LinkedHashMap<>();

		public String idOf(RuleVertex vertex) {
			return idMap.computeIfAbsent(vertex, it -> "id"+idMap.size());
		}
	}

	private static DefaultDirectedGraph<RuleVertex, DefaultEdge> buildGraph(CalculationMap calculations, ValidationMap validations) {
		Wrapper builder=new Wrapper();

		Set<ValueSource<?>> allSources = Stream.concat(
			calculations.values().stream()
				.flatMap(it -> it.sources().stream()),
			validations.values().stream()
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

		calculations.values().forEach(calculation -> builder.add(calculation.destination(), calculation, null));
		validations.values().forEach(validation -> builder.add(validation.destination(), null, validation));

		return builder.build();
	}

	private static class Wrapper {
		private final Graphs.GraphBuilder<RuleVertex, DefaultEdge, DefaultDirectedGraph<RuleVertex, DefaultEdge>> builder = Graphs.graphBuilder(
			Graphs.<RuleVertex>directedGraph()).get();

		Wrapper add(
			Value<?> destination,
			@Nullable
			Calculation<?> calculation,
			@Nullable
			Validation<?> validation
		) {
			RuleVertex destinationVertex = RuleVertex.value(destination);
			builder.addVertex(destinationVertex);

			if (calculation!=null) {
				RuleVertex calculationVertex = RuleVertex.calculation(calculation);
				builder.addVertex(calculationVertex);
				builder.addEdge(calculationVertex, destinationVertex);

				calculation.sources().forEach(source -> {
					RuleVertex sourceVertex = RuleVertex.value(source);
					builder.addVertex(sourceVertex);
					builder.addEdge(sourceVertex, calculationVertex);
				});
			}

			if (validation!=null) {
				RuleVertex validationVertex = RuleVertex.validation(validation);
				builder.addVertex(validationVertex);
				builder.addEdge(validationVertex, destinationVertex);

				validation.sources().forEach(source -> {
					RuleVertex sourceVertex = RuleVertex.value(source);
					builder.addVertex(sourceVertex);
					builder.addEdge(sourceVertex, validationVertex);
				});
			}

			return this;
		}

		public DefaultDirectedGraph<RuleVertex, DefaultEdge> build() {
			return builder.build();
		}
	}
}
