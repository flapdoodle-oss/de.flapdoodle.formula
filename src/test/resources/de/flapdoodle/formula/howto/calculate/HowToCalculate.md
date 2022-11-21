# Calculate Values

As we are trying to use some 'higher magic' (a graph :)) to bring all calculations in the right order, we must
provide some additional information beside the formula. The easiest way is by using a fluent calculation builder:

```java
${fluentWay.sample}
```

As there are much more possibilities, here are the building blocks. First of all we are
starting with the value we want to calculate:

```java
${basics.destination}
```
... then we can express, if all source values are required or maybe null:

```java
${basics.sources}
```

... with that there are some ways to deal with null values:                          

```java
${basics.mapping}
```
                                                           
## Custom Calculations

If this does not fit your needs, you can provide your own calculation implementation:

```java
${customImplementation.SumCalculation}
```

... and provide the needed parameters:

```java
${customImplementation.sample}
```

## No need to use Lambdas

You can use your own implementation of a function with a matching type signature. You should use a singleton for this as
all implementations should be side effect free:

```java
${fluentWithFunctionImplementation.SumFunction}
```

... and use this as a replacement for a lambda function:                                               

```java
${fluentWithFunctionImplementation.sample}
```