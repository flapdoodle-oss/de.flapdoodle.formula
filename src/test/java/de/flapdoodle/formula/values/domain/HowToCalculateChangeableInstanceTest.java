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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.solver.GraphBuilder;
import de.flapdoodle.formula.solver.GraphRenderer;
import de.flapdoodle.formula.solver.Solver;
import de.flapdoodle.formula.solver.ValueGraph;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.types.TypeCounter;
import de.flapdoodle.testdoc.Includes;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToCalculateChangeableInstanceTest {

	@RegisterExtension
	public static Recording recording = Recorder.with("HowToCalculateChangeableInstanceTest.md", TabSize.spaces(2));


	private Id.ClearTypeCounter clearTypeCounter;

	@BeforeEach
	void localTypeCounter() {
		clearTypeCounter = Id.with(new TypeCounter());
	}

	@AfterEach
	void clearStuff() {
		Preconditions.checkNotNull(clearTypeCounter,"clearTypeCounter not set");
		clearTypeCounter.close();
	}

	@Test
	void sumOfItemsInCart() {
		recording.include(Value.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.include(Cart.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.include(Item.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.include(CartValueLookup.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		
		recording.begin("domainobject");
		Cart cart = Cart.builder()
			.addItems(Item.builder().name("box").quantity(2).price(10.5).build())
			.addItems(Item.builder().name("book").quantity(1).price(9.95).build())
			.addItems(Item.builder().name("nail").quantity(10).price(2.55).build())
			.build();
		recording.end();

		recording.begin("graph");
		ValueGraph valueGraph = GraphBuilder.build(cart.addRulesTo(Rules.empty()));
		recording.end();

		recording.begin("render");
		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph(), HasHumanReadableLabel::asHumanReadable);
		recording.end();
		recording.output("render.dot", dot);

		recording.begin("solve");
		Solver.Result result = Solver.solve(
			valueGraph,
			new CartValueLookup(cart)
		);
		recording.end();

		recording.begin("change");
		Cart updated = cart;

		for (Value<?> id : result.validatedValues()) {
			if (id instanceof ChangeableValue) {
				updated = updated.change((ChangeableValue) id, result.get(id));
			}
		}
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
}
