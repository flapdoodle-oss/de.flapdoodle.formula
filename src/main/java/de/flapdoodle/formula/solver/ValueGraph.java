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
import de.flapdoodle.formula.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

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

  public Set<Value<?>> calculationDestinations() {
    return calculationMap.keys();
  }

	public DefaultDirectedGraph<Value<?>, DefaultEdge> graph() {
		return graph;
	}
}
