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
package de.flapdoodle.formula.howto.calculate;

import de.flapdoodle.formula.types.Id;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class Sample {
  @Value.Default
  public Id<Sample> getId() {
    return Id.idFor(Sample.class);
  }

  public abstract @Nullable String getName();

  public abstract @Nullable Integer getNumber();

  public abstract @Nullable Double getAmount();

  public abstract Sample withNumber(Integer number);

  public static ImmutableSample.Builder builder() {
    return ImmutableSample.builder();
  }
}
