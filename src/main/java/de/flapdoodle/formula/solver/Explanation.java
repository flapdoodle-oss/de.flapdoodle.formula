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

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.Validation;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Immutable
public abstract class Explanation {
	public abstract List<ExplainValue<?>> list();

	@Immutable
	public static abstract class ExplainValue<T> {
		public abstract Value<T> destination();

		public abstract Optional<Validation<T>> validation();
		public abstract Optional<Calculation<T>> calculation();
	}

	public static <T> ExplainValue explainValue(Value<T> destination, @Nullable Validation<T> validation, @Nullable Calculation<T> calculation) {
		return ImmutableExplainValue.<T>builder()
			.destination(destination)
			.validation(Optional.ofNullable(validation))
			.calculation(Optional.ofNullable(calculation))
			.build();
	}

	public static ImmutableExplanation.Builder builder() {
		return ImmutableExplanation.builder();
	}

	public static String render(Explanation explanation) {
		return render(explanation, HasHumanReadableLabel::asHumanReadable);
	}

	public static String render(Explanation explanation, Function<Value<?>, String> renderValue) {
		StringBuilder sb=new StringBuilder();
		//render(sb, tree, 0, renderValue);
		explanation.list().forEach(explainValue -> {
			sb.append(renderValue.apply(explainValue.destination())).append("\n");
			explainValue.validation().ifPresent(validation -> {
				sb.append(" check if ").append(HasHumanReadableLabel.asHumanReadable(validation)).append("\n");
				validation.sources().forEach(source -> {
					sb.append(" - ").append(renderValue.apply(source)).append("\n");
				});
			});
			explainValue.calculation().ifPresent(calculation -> {
				sb.append(" calculate with ").append(HasHumanReadableLabel.asHumanReadable(calculation)).append("\n");
				calculation.sources().forEach(source -> {
					sb.append(" - ").append(renderValue.apply(source)).append("\n");
				});
			});
			sb.append("\n");
		});
		return sb.toString();
	}

}
