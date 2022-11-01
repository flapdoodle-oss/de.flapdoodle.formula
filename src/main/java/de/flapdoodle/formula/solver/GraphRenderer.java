package de.flapdoodle.formula.solver;

import com.google.common.collect.ImmutableMap;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.graph.GraphAsDot;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class GraphRenderer {
	private GraphRenderer() {
		// no instance
	}

	public static String renderGraphAsDot(DefaultDirectedGraph<Value<?>, DefaultEdge> graph, Function<Value<?>, String> labelOfValue) {
		IdGenerator idGenerator=new IdGenerator();
		return GraphAsDot.<Value<?>>builder(idGenerator::idOf)
			.nodeAttributes(it -> ImmutableMap.<String, String>builder()
				.putAll(colorOf(it))
				.put("label", labelOfValue.apply(it))
				.build())
			.label("calculation")
			.build()
			.asDot(graph);
	}

	private static Map<String, String> colorOf(Value<?> node)  {
		return ImmutableMap.<String, String>builder()
			.put("fillcolor",(node instanceof Unvalidated) ? "gray81" : "lightskyblue")
			.put("style","filled")
			.put("shape","rectangle")
			.build();
	}

	private static class IdGenerator {
		private final Map<Value<?>, String> idMap=new LinkedHashMap<>();

		public String idOf(Value<?> id) {
			return idMap.computeIfAbsent(id, it -> "id"+idMap.size());
		}
	}
}
