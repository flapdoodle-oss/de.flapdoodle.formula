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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.rules.CalculationMap;
import de.flapdoodle.formula.rules.ValidationMap;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.graph.Graphs;
import de.flapdoodle.graph.Loop;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValueGraph {
	private final DefaultDirectedGraph<Value<?>, DefaultEdge> graph;
	private final CalculationMap calculationMap;
	private final ValidationMap validationMap;

	// only visible in this package
	ValueGraph(
		DefaultDirectedGraph<Value<?>, DefaultEdge> graph,
		CalculationMap calculationMap,
		ValidationMap validationMap
	) {
		this.graph = graph;
		this.calculationMap = calculationMap;
		this.validationMap = validationMap;

		List<Loop<Value<?>, DefaultEdge>> loops = Graphs.rootsOf(graph).stream()
			.flatMap(it -> it.loops().stream())
			.collect(Collectors.toList());

		if (!loops.isEmpty()) {
			String asDot = GraphRenderer.renderGraphAsDot(graph);
			throw new IllegalArgumentException(Strings.lenientFormat("loops detected: %s\n-8<-------\n%s\n->8-------\n", loops, asDot));
		}
	}

	public <T> Calculation<T> calculation(Value<T> key) {
		return Preconditions.checkNotNull(calculationMap.get(key),"calculation for %s not found", key);
	}

	public <T> Calculation<T> calculationOrNull(Value<T> key) {
		return calculationMap.get(key);
	}

	public <T> Validation<T> validation(Value<T> key) {
		return Preconditions.checkNotNull(validationMap.get(key),"validation for %s not found", key);
	}

	public <T> Validation<T> validationOrNull(Value<T> key) {
		return validationMap.get(key);
	}

	public DefaultDirectedGraph<Value<?>, DefaultEdge> graph() {
		return graph;
	}

	public Explanation explain(Value<?> destination) {
		Preconditions.checkArgument(graph.containsVertex(destination),"value not found: %s", destination);

		Set<Value<?>> explainedValues = new LinkedHashSet<>();

		return Explanation.builder()
			.addAllList(explain(explainedValues, destination))
			.build();
	}

	private <T> List<Explanation.ExplainValue<?>> explain(Set<Value<?>> explainedValues, Value<T> destination) {
		ImmutableList.Builder<Explanation.ExplainValue<?>> builder = ImmutableList.builder();
		if (!explainedValues.contains(destination)) {
			explainedValues.add(destination);

			Validation<T> validation = validationMap.get(destination);
			ImmutableList.Builder<Explanation.ExplainValue<?>> validationListBuilder=ImmutableList.builder();
			if (validation != null) {
				for (ValueSource<?> source : validation.sources()) {
					validationListBuilder.addAll(explain(explainedValues, source));
				}
			}

			Calculation<T> calculation = calculationMap.get(destination);
			ImmutableList.Builder<Explanation.ExplainValue<?>> calculationListBuilder=ImmutableList.builder();
			if (calculation != null) {
				for (ValueSource<?> source : calculation.sources()) {
					calculationListBuilder.addAll(explain(explainedValues, source));
				}
			}

			builder.add(Explanation.explainValue(destination, validation, calculation));
			builder.addAll(validationListBuilder.build());
			builder.addAll(calculationListBuilder.build());
		}
		return builder.build();
	}

}
