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
