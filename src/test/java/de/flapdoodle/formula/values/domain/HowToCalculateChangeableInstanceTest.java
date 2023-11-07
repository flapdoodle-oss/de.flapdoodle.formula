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
package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.AbstractHowToTest;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.explain.RuleDependencyGraph;
import de.flapdoodle.formula.rules.Rules;
import de.flapdoodle.formula.solver.*;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.testdoc.Includes;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToCalculateChangeableInstanceTest extends AbstractHowToTest {

	@RegisterExtension
	public static Recording recording = Recorder.with("HowToCalculateChangeableInstanceTest.md", TabSize.spaces(2));

	@Test
	void sumOfItemsInCart() {
		recording.include(Value.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.include(Cart.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.include(Item.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.include(ChangeableInstanceValueLookup.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		
		recording.begin("domainobject");
		Cart cart = Cart.builder()
			.addItems(Item.builder().name("box").quantity(2).price(10.5).build())
			.addItems(Item.builder().name("book").quantity(1).price(9.95).build())
			.addItems(Item.builder().name("nail").quantity(10).price(2.55).build())
			.build();
		recording.end();

		recording.begin("graph");
		Rules rules = cart.addRulesTo(Rules.empty());
		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(rules);
		recording.end();

		recording.begin("render");
		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph());
		recording.end();
		recording.output("render.dot", dot);
		recording.file("render.dot.svg", "HowToCalculateChangeableInstanceTest.svg", asSvg(dot));

		recording.begin("explain");
		String explainDot = RuleDependencyGraph.explain(rules);
		recording.end();
		recording.output("explain.dot", explainDot);
		recording.file("explain.dot.svg", "HowToCalculateChangeableInstanceTest-explained.svg", asSvg(explainDot));

		recording.begin("solve");
		Result result = Solver.solve(
			valueGraph,
			ChangeableInstanceValueLookup.of(cart, ValueLookup.failOnEachValue())
		);
		recording.end();

		recording.begin("explain-value");
		Explanation explanation = valueGraph.explain(Cart.sumWithoutTax.withId(cart.id()));
		String explainSumWithoutTax = Explanation.render(explanation, value -> HasHumanReadableLabel.asHumanReadable(value)
			+ " = "
			+ result.valueOrError(value)
			.map(it -> "" + it, errors -> "" + errors.errorMessages().stream()
				.map(errorMessage -> "error('" + errorMessage.key() + "')")
				.collect(Collectors.joining(", "))));
		recording.end();
		recording.output("explain-value.text", explainSumWithoutTax);

		recording.begin("change");
		Cart updated = ChangeableInstance.change(cart, result);
		recording.end();

		recording.begin("check");
		assertThat(updated.items().get(0).sum()).isEqualTo(2 * 10.5);
		assertThat(updated.items().get(0).isCheapest()).isFalse();

		assertThat(updated.items().get(1).sum()).isEqualTo(1 * 9.95);
		assertThat(updated.items().get(1).isCheapest()).isTrue();

		assertThat(updated.items().get(2).sum()).isEqualTo(10 * 2.55);
		assertThat(updated.items().get(2).isCheapest()).isFalse();

		assertThat(updated.sumWithoutTax())
			.isEqualTo(2 * 10.5 + 9.95 + 10 * 2.55);
		recording.end();
	}

	private byte[] asSvg(String dot) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Graphviz.fromString(dot)
//				.width(3200)
				.render(Format.SVG_STANDALONE)
				.toOutputStream(os);
			return os.toByteArray();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
