# Values
        
A `Value<T>` describes a unique address for a value of certain type `T`. A `ValueSource<T>` should address where a value is present and
a `ValueSink<T>` should address a value which will be created. 

```java
${values.types}
```

As you can create your own `Value` implementations, they must implement the `equals()`/`hashCode()` contract. Or you can use these implementations:

```java
${values.named}
```

## Scope

As you can not avoid name collisions with a growing number of values easily, one way to solve this kind of problem is to use
any object as a scope:

```java
${valuesRelated.relatedTo}
```

For some cases you can not use an object as scope, but you need some kind of id:                     

```java
${valuesRelated.idFactory}
```

## Get the Value

The `Value<T>` is used to get a value for this address. For this we need an `ValueLookup`:

```java
${valueLookup.sample}
```

I would recommend that any implementation of `ValueLookup` should fail if a value can not be found. There
is already an implementation where you can provide a list of known values:

```java
${valueLookup.strict}
```

## Object Binding

Most of the time your data is stored in some kind of object. In this example `SampleBean` is used as a mutable instance with
properties of different types:

```java
${properties.SampleBean}
```

To abstract the access to any of these properties you could use lamdas. But as lambdas are unique instances they are not equal to each other
even if the same syntax is used:

```java
${properties.facts}
```

So if you use a lambda as part of any instance which must implement the `equals()`/`hashCode()` contract,
you should create a singleton for this (static field).

As lambdas don't provide any useful information (f.I. method called), you should provide a meaningful label, if you use
`ReadOnlyProperty` as a parameter:

```java
${properties.readOnly}
```

If we have a property abstraction, it is a very tiny step to create an `Value` from this:

```java
${properties.readOnly.asValue}
```

But if you want more than just read a value, then you can use a `ModifiableProperty`:                                                                                         

```java
${properties.modifiable}
```

... and with that it is easy to create an `Value`:                       

```java
${properties.modifiable.asValue}
```

## Immutables

If you prefer immutable data structures to avoid many kind of troubles, only minor changes are needed:

```java
${immutables.Sample}
```
([immutables.github.io](https://immutables.github.io) is used to generate immutable implementation of abstract classes or interfaces)
                               
As reading from a property for an immutable object does not differ from a mutable object, the code does not change much:

```java
${immutables.readOnly}
```

... and so the way to make this to a `Value`:

```java
${immutables.readOnly.asValue}
```

But as immutable object can not be modified, we must create a modified version on any change:

```java
${immutables.copyOnChange}
```

... and so if we create a `Value` from this:                                       

```java
${immutables.copyOnChange.asValue}
```

## Put all together

One way to put all pieces together:

```java
${valueLookupWithProperties.sample}
```

... but as this will become a pattern as you will have more than one object to work on you
can use some useful abstractions to reduce the boilerplate code. Put the properties where they belong:

```java
${changeableInstance.ChangeableSample}
```

... and then you can use it: 

```java
${changeableInstance.sample}
```
                                            

## Summary

This is one way to deal with addresses described with `Value` instances. Why now access a rest service? Or a database?
Feel free to use it your way.
