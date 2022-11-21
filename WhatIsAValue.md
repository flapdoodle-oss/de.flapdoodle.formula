# Values
        
A `Value<T>` describes a unique address for a value of certain type `T`. A `ValueSource<T>` should address where a value is present and
a `ValueSink<T>` should address a value which will be created. 

```java
Value<Integer> value=new Value<Integer>() {};
Value<Integer> valueSink=new ValueSink<Integer>() {};
Value<Integer> valueSource=new ValueSource<Integer>() {};
```

As you can create your own `Value` implementations, they must implement the `equals()`/`hashCode()` contract. Or you can use these implementations:

```java
Value<Double> a = Value.named("a", Double.class);
Value<Double> otherA = Value.named("a", Double.class);
Value<Double> b = Value.named("b", Double.class);

assertThat(a).isInstanceOf(ValueSource.class);
assertThat(a).isInstanceOf(ValueSink.class);

assertThat(a).isNotEqualTo(b);
assertThat(a).isEqualTo(otherA);
```

## Scope

As you can not avoid name collisions with a growing number of values easily, one way to solve this kind of problem is to use
any object as a scope:

```java
Value<Double> a = Value.named("a", Double.class);

Value<Double> aRelatedToX = a.relatedTo("X");
Value<Double> aRelatedToY = a.relatedTo("Y");
assertThat(aRelatedToX).isNotEqualTo(aRelatedToY);
```

For some cases you can not use an object as scope, but you need some kind of id:                     

```java
Id<Double> firstId = Id.idFor(Double.class);
Id<Double> secondId = Id.idFor(Double.class);
assertThat(firstId).isNotEqualTo(secondId);

Value<Double> relatedToFirstId = a.relatedTo(firstId);
Value<Double> relatedToSecondId = a.relatedTo(secondId);
assertThat(relatedToFirstId).isNotEqualTo(relatedToSecondId);
```

## Get the Value

The `Value<T>` is used to get a value for this address. For this we need an `ValueLookup`:

```java
Value<Double> a = Value.named("a", Double.class);
Value<Integer> b = Value.named("b", Integer.class);

ValueLookup valueLookup=new ValueLookup() {
  @Override public <T> @Nullable T get(Value<T> value) {
    if (value == a) {
      return (T) Double.valueOf(1.0);
    }
    throw new IllegalArgumentException("not found: "+value);
  }
};

assertThat(valueLookup.get(a)).isEqualTo(1.0);
assertThatThrownBy(() -> valueLookup.get(b))
  .isInstanceOf(IllegalArgumentException.class);
```

I would recommend that any implementation of `ValueLookup` should fail if a value can not be found. There
is already an implementation where you can provide a list of known values:

```java
StrictValueLookup strictValueLookup = StrictValueLookup.of(
  MappedValue.of(a, 2.0)
);

assertThat(strictValueLookup.get(a)).isEqualTo(2.0);
assertThatThrownBy(() -> strictValueLookup.get(b))
  .isInstanceOf(IllegalArgumentException.class);
```

## Object Binding

Most of the time your data is stored in some kind of object. In this example `SampleBean` is used as a mutable instance with
properties of different types:

```java
public class SampleBean {
  private String name;
  private Integer number;
  private Double amount;
  private Id<SampleBean> id = Id.idFor(SampleBean.class);

  public Id<SampleBean> getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Integer getNumber() {
    return number;
  }
  public void setNumber(Integer number) {
    this.number = number;
  }
  public Double getAmount() {
    return amount;
  }
  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
```

To abstract the access to any of these properties you could use lamdas. But as lambdas are unique instances they are not equal to each other
even if the same syntax is used:

```java
Function<SampleBean, Double> getterAsFunction = SampleBean::getAmount;
Function<SampleBean, Double> secondInstance = SampleBean::getAmount;
assertThat(getterAsFunction).isNotEqualTo(secondInstance);
```

So if you use a lambda as part of any instance which must implement the `equals()`/`hashCode()` contract,
you should create a singleton for this (static field).

As lambdas don't provide any useful information (f.I. method called), you should provide a meaningful label, if you use
`ReadOnlyProperty` as a parameter:

```java
ReadOnlyProperty<SampleBean, Double> property = ReadOnlyProperty.of(SampleBean.class, "amount", SampleBean::getAmount);

SampleBean bean = new SampleBean();
bean.setAmount(123.0);

assertThat(property.get(bean)).isEqualTo(123.0);
```

If we have a property abstraction, it is a very tiny step to create an `Value` from this:

```java
ReadOnlyValue<SampleBean, Double> amountValue = property.withId(bean.getId());
assertThat(amountValue.get(bean)).isEqualTo(123.0);
assertThat(amountValue.id()).isEqualTo(bean.getId());
```

But if you want more than just read a value, then you can use a `ModifiableProperty`:                                                                                         

```java
ModifiableProperty<SampleBean, Integer> modifiable = ModifiableProperty.of(SampleBean.class, "number",
  SampleBean::getNumber, SampleBean::setNumber);

assertThat(modifiable.get(bean)).isNull();
modifiable.set(bean,42);
assertThat(modifiable.get(bean)).isEqualTo(42);
```

... and with that it is easy to create an `Value`:                       

```java
ModifyInstanceValue<SampleBean, Integer> numberValue = modifiable.withId(bean.getId());
assertThat(numberValue.get(bean)).isEqualTo(42);
assertThat(numberValue.id()).isEqualTo(bean.getId());
numberValue.set(bean,13);
assertThat(numberValue.get(bean)).isEqualTo(13);
```

## Immutables

If you prefer immutable data structures to avoid many kind of troubles, only minor changes are needed:

```java
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
```
([immutables.github.io](https://immutables.github.io) is used to generate immutable implementation of abstract classes or interfaces)
                               
As reading from a property for an immutable object does not differ from a mutable object, the code does not change much:

```java
ReadOnlyProperty<Sample, Double> property = ReadOnlyProperty.of(Sample.class, "amount", Sample::getAmount);

Sample instance = Sample.builder()
        .amount(123.0)
        .build();

assertThat(property.get(instance)).isEqualTo(123.0);
```

... and so the way to make this to a `Value`:

```java
ReadOnlyValue<Sample, Double> amountValue = property.withId(instance.getId());
assertThat(amountValue.get(instance)).isEqualTo(123.0);
assertThat(amountValue.id()).isEqualTo(instance.getId());
```

But as immutable object can not be modified, we must create a modified version on any change:

```java
CopyOnChangeProperty<Sample, Integer> changeable = CopyOnChangeProperty.of(Sample.class, "number",
        Sample::getNumber, Sample::withNumber);

assertThat(changeable.get(instance)).isNull();
Sample changedInstance = changeable.change(instance, 42);
assertThat(changedInstance.getNumber()).isEqualTo(42);
assertThat(instance.getNumber()).isNull();
```

... and so if we create a `Value` from this:                                       

```java
CopyOnChangeValue<Sample, Integer> numberValue = changeable.withId(instance.getId());
assertThat(numberValue.get(changedInstance)).isEqualTo(42);
assertThat(numberValue.id()).isEqualTo(instance.getId());
assertThat(numberValue.id()).isEqualTo(changedInstance.getId());
```

## Put all together

One way to put all pieces together:

```java
Sample instance = Sample.builder()
  .amount(123.0)
  .build();

ReadOnlyValue<Sample, Double> amountValue = ReadOnlyProperty
  .of(Sample.class, "amount", Sample::getAmount)
  .withId(instance.getId());
CopyOnChangeValue<Sample, Integer> numberValue = CopyOnChangeProperty
  .of(Sample.class, "number", Sample::getNumber, Sample::withNumber)
  .withId(instance.getId());

ValueLookup delegatingValueLookup = new ValueLookup() {
  @Override public <T> @Nullable T get(Value<T> value) {
    if (value instanceof ReadableValue) {
      ReadableValue<?, T> readableValue = (ReadableValue<?, T>) value;
      if (readableValue.id().equals(instance.getId())) {
        ReadableValue<Sample, T> readFromSample = (ReadableValue<Sample, T>) readableValue;
        return readFromSample.get(instance);
      }
    }
    throw new IllegalArgumentException("not found: "+value);
  }
};

assertThat(delegatingValueLookup.get(amountValue)).isEqualTo(123.0);
assertThat(delegatingValueLookup.get(numberValue)).isNull();
```

... but as this will become a pattern as you will have more than one object to work on you
can use some useful abstractions to reduce the boilerplate code. Put the properties where they belong:

```java
@Value.Immutable
public abstract class ChangeableSample implements ChangeableInstance<ChangeableSample> {
  public static ReadOnlyProperty<ChangeableSample, String> name =
    readOnly(ChangeableSample.class, "name", ChangeableSample::name);
  public static ReadOnlyProperty<ChangeableSample, Double> amount =
    readOnly(ChangeableSample.class, "amount", ChangeableSample::amount);
  public static CopyOnChangeProperty<ChangeableSample, Integer> number =
    copyOnChange(ChangeableSample.class, "number", ChangeableSample::number, ChangeableSample::withNumber);

  @Value.Default
  public Id<ChangeableSample> id() {
    return Id.idFor(ChangeableSample.class);
  }

  public abstract @Nullable String name();

  public abstract @Nullable Integer number();

  public abstract @Nullable Double amount();

  public abstract ChangeableSample withNumber(Integer number);

  @Override
  public <T> ChangeableSample change(ChangeableValue<?, T> id, T value) {
    if (id.id().equals(id())) {
      return ((ChangeableValue<ChangeableSample, T>) id).change(this, value);
    }
    return this;
  }

  @Override
  public <T> Maybe<T> findValue(ReadableValue<?, T> id) {
    if (id.id().equals(id())) {
      return Maybe.some(((ReadableValue<ChangeableSample, T>) id).get(this));
    }
    return Maybe.none();
  }

  public static ImmutableChangeableSample.Builder builder() {
    return ImmutableChangeableSample.builder();
  }
}
```

... and then you can use it: 

```java
ChangeableSample instance = ChangeableSample.builder()
  .name("name")
  .number(42)
  .amount(123.0)
  .build();

ValueLookup valueLookup = ChangeableInstanceValueLookup.of(
  instance, ValueLookup.failOnEachValue()
);

assertThat(valueLookup.get(ChangeableSample.name.withId(instance.id())))
  .isEqualTo("name");
assertThat(valueLookup.get(ChangeableSample.amount.withId(instance.id())))
  .isEqualTo(123.0);
```
                                            

## Summary

This is one way to deal with addresses described with `Value` instances. Why now access a rest service? Or a database?
Feel free to use it your way.