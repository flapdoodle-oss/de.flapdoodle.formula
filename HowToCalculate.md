# Calculate Values

As we are trying to use some 'higher magic' (a graph :)) to bring all calculations in the right order, we must
provide some additional information beside the formula. The easiest way is by using a fluent calculation builder:

```java
ValueSource<Integer> valueA = Value.named("a", Integer.class);
ValueSource<Integer> valueB = Value.named("b", Integer.class);
ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

Calculation<Integer> calculateSum = Calculate.value(valueSum)
  .using(valueA, valueB)
  .by((a, b) -> (a!=null && b!=null) ? a + b : null);

Integer sum = calculateSum.calculate(valueLookup(
  MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
));

assertThat(sum).isEqualTo(3);
```

As there are much more possibilities, here are the building blocks. First of all we are
starting with the value we want to calculate:

```java
ValueSource<Integer> valueA = Value.named("a", Integer.class);
ValueSink<Integer> valueResult = Value.named("result", Integer.class);

Calculate.WithDestination<Integer> withDestination = Calculate.value(valueResult);
```
... then we can express, if all source values are required or maybe null:

```java
Map1<Integer, Integer> direct = withDestination.from(valueA);
Calculate.WithMap1Nullable<Integer, Integer> usingA = withDestination.using(valueA);
Calculate.WithMap1<Integer, Integer> requiringA = withDestination.requiring(valueA);
```

... with that there are some ways to deal with null values:                          

```java
Calculation<Integer> calculation;

calculation  = direct;
assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1))))
  .isEqualTo(1);
assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null))))
  .isNull();

calculation = usingA.by(a -> (a!=null) ? a + 1 : null);
assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1))))
  .isEqualTo(2);
assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null))))
  .isNull();

calculation = usingA.ifAllSetBy(a -> a +1);
assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1))))
  .isEqualTo(2);
assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null))))
  .isNull();

assertThat(requiringA.by(a -> a +1).calculate(valueLookup(MappedValue.of(valueA,1))))
  .isEqualTo(2);

assertThatThrownBy(() -> {
  requiringA.by(a -> a + 1).calculate(valueLookup(MappedValue.of(valueA, null)));
})
  .isInstanceOf(NullPointerException.class)
  .hasMessageContaining( "a(Integer) is null");
```
                                                           
## Custom Calculations

If this does not fit your needs, you can provide your own calculation implementation:

```java
@Immutable
public abstract class SumCalculation implements Calculation<Integer> {
  public abstract ValueSource<Integer> a();
  public abstract ValueSource<Integer> b();

  @Override
  public abstract Value<Integer> destination();

  @Override
  @org.immutables.value.Value.Lazy
  public Set<? extends ValueSource<?>> sources() {
    return ImmutableSet.of(a(), b());
  }

  @Override
  public Integer calculate(ValueLookup values) {
    Integer a = values.get(a());
    Integer b = values.get(b());
    return (a != null && b != null)
      ? a + b
      : null;
  }

  public static ImmutableSumCalculation.Builder builder() {
    return ImmutableSumCalculation.builder();
  }
}
```

... and provide the needed parameters:

```java
ValueSource<Integer> valueA = Value.named("a", Integer.class);
ValueSource<Integer> valueB = Value.named("b", Integer.class);
ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

ImmutableSumCalculation calculateSum = SumCalculation.builder()
  .a(valueA)
  .b(valueB)
  .destination(valueSum)
  .build();

Integer sum = calculateSum.calculate(valueLookup(
  MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
));

assertThat(sum).isEqualTo(3);
```

## No need to use Lambdas

You can use your own implementation of a function with a matching type signature. You should use a singleton for this as
all implementations should be side effect free:

```java
public class SumFunction implements FN2<Integer, Integer, Integer> {
  private SumFunction() {
    // no instance
  }

  @Nullable @Override
  public Integer apply(@Nullable Integer a, @Nullable Integer b) {
    return (a != null && b != null)
      ? a + b
      : null;
  }

  private final static SumFunction INSTANCE=new SumFunction();

  public static SumFunction getInstance() {
    return INSTANCE;
  }
}
```

... and use this as a replacement for a lambda function:                                               

```java
ValueSource<Integer> valueA = Value.named("a", Integer.class);
ValueSource<Integer> valueB = Value.named("b", Integer.class);
ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

Calculation<Integer> calculateSum = Calculate.value(valueSum)
  .using(valueA, valueB)
  .by(SumFunction.getInstance());

Integer sum = calculateSum.calculate(valueLookup(
  MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
));

assertThat(sum).isEqualTo(3);
```