# How to execute Calculations on Immutable Object Trees

If you want to so some calculations in object trees, things can get messy. To make things easier, we can use a graph to
determine where to start and how to propagate further, we separate calculation and procurement and mix even validation
into that. Each calculation must declare which value is calculated and which dependencies are used for this:

```java
${sumOfItemsInCart.Value}
```
As you can provide your own implementation, each value must implement `hashcode/equals`.

In this sample we use an immutable shopping cart which we change with the calculation result..

As this stuff should run on java 8, we use [immutables.org](http://immutables.org) instead of Lombok or java records.

## Let's create a Shopping Cart                                                                                              

We need an item class, which stores properties like `quantity` and `price`.

```java
${sumOfItemsInCart.Item}
```
As you can see, there are some static declarations with lambdas for accessing the matching instance values. As lambdas
does not equal if f.I. the method call is used, we must use singletons.

We also need a cart class, where all items are stored:

```java
${sumOfItemsInCart.Cart}
```

We want to calculate the sum of each item, the sum of all items and mark the cheapest item in this cart.
In this example we are using some generic sample data:

```java
${sumOfItemsInCart.domainobject}
```

With this sample data we can create all rules, as you can see in the `addRulesTo` method. Each instance has an unique id, 
which should not change if we change any value. With all rules we can create a graph from it:

```java
${sumOfItemsInCart.graph}
```
                                                                                          
If a graph can be build, you can render it as a [graphviz/dot](https://graphviz.org/doc/info/lang.html) graph:

```java
${sumOfItemsInCart.render}
```

.. which results in:

```text
${sumOfItemsInCart.render.dot}
```

![Calculation as Graph](HowToCalculateChangeableInstanceTest.png)

Or you can render a more detailed graph:

```java
${sumOfItemsInCart.explain}
```

.. which results in:

```text
${sumOfItemsInCart.explain.dot}
```

![Calculation as Graph - explained](HowToCalculateChangeableInstanceTest-explained.png)

## Do the Math                     

We still need some glue code to get the values from the current shopping cart:

```java
${sumOfItemsInCart.CartValueLookup}
```

With all this in place we can solve all equations.

```java
${sumOfItemsInCart.solve}
```

We can inspect how a value is calculated...                                

```java
${sumOfItemsInCart.explain-value}
```

... and can produce some readable output:
```
${sumOfItemsInCart.explain-value.text}
```

We can inspect validation errors or just apply all results back to
our shopping cart:

```java
${sumOfItemsInCart.change}
```

## Trust is good, Control is better

As this documentation is generated from a running test, we can prove that
all calculations are done the right way:

```java
${sumOfItemsInCart.check}
```
