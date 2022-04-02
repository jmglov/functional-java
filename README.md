# functional-java

A demonstration of basic functional programming concepts in Java 17.

## What is functional programming?

This is a very controversial topic, but let's define functional programming around the following four building blocks:
1. Higher-order functions
2. Pure functions
3. Immutable data
4. Referential transparency

Let's dig into these one at a time.

**Higher-order functions** are functions that take other functions as arguments. This may seem odd, but should become
more clear once we look at a few examples later.

**Pure functions** are functions that always return the same result when called with the same argument. All mathematical
functions are pure functions; if you have a function `f` such that `f(x) -> x + 1`, `f(1)` will always be `2`.

**Immutable data** is data that once defined, can never be changed. Numbers are a trivial example of immutable data;
`1` is always `1` and can never be changed, or it is no longer `1`, it's something else.

**Referential transparency** means that a function can be replaced with its definition without changing the semantics of
the code. Taking the function `f` above, we can see that it is referentially transparent because replacing any and all
occurrences of `f(1)` with in our program with `1 + 1` will not change its semantics (but may of course change its
performance characteristics, as inline code is typically faster than a function call). In order for this to be possible,
we need pure functions operating on immutable data.

## Is Java a functional programming language?

We can define a functional programming language in (at least) one of two ways:
1. A language that requires programming in a functional style
2. A language that supports programming in a functional style

Let's walk through our building blocks of functional programming and see what Java offers for each.

1. Higher-order functions: Java doesn't have first-class functions like Haskell or Clojure (or even Python and Perl),
   but one can define a class called something like `Function`, give it an instance method called `apply`, and all of a
   sudden, you have a function wrapped up in an object that you can pass around. So we can say that Java supports
   higher-order functions, if you are a bit generous with your definition of "function".
2. Pure functions: Java certainly allows you to write pure functions, but it also doesn't stop you from writing impure
   functions either. So Java supports but doesn't require pure functions. An example of a language that actually
   requires pure functions is Haskell.
3. Immutable data: Java has some immutable data types like `String` and `Integer` and so forth, but most common data
   types in Java are very mutable indeed (like `List` or `Map`). Java certainly allows you to create your own immutable
   data types, but doesn't require that all data is immutable, like Haskell or Clojure.
4. Referential transparency: if all of your Java functions are pure and operate on immutable data, they are also
   referentially transparent. Java's just in time compiler (JIT) will actually inline functions at runtime when it
   can tell it's safe to do so and it determines that it would yield a performance improvement. Again, we can be
   generous and claim that Java supports referential transparency.

As Java doesn't require any of the building blocks of functional programming, it would be quite a stretch to call it a
functional programming language. Having said that, we've seen that Java provides varying degrees of support for all of
them, so it's certainly possible to **do** functional programming in Java. In fact, it's getting easier and easier with
each Java release, as we shall see in due course.

## Getting down to brass tacks

Let's start by talking about higher-order functions. What better higher order functions to implement than those old
functional programming favourites, `map`, `filter`, and `reduce` (called `fold` by certain barbarians such as Erlang and
Haskell developers).
* `map` takes a list of N things and a function and returns another list of N things, which are the result of calling
  the function on each of the items in the input list. A popular example of this is: mapping over a list of 25 apples
  with a function that takes an apple and returns a banana would result in a list of 25 bananas.
* `filter` takes a list of N things and a function that returns a boolean value when called with a list item and returns
  a list of the items in the list for which calling the function on returns `true`. For example, filtering a list of
  fruits containing 14 apples and 11 bananas with a function that returns true if called with an apple will return a
  list of 14 apples.
* `reduce` is perhaps the trickiest to understand. It takes a list of N things, a function, and an initial value, and
  returns whatever you want. How it accomplishes this is by calling the function for each item of the list with two
  arguments: the item itself, and something usually called an "accumulator". For the first list item, the accumulator
  will be the initial value you provided to `reduce`, and for subsequent items, it will be the value returned by calling
  the function with the previous item and the accumulator. For example, reducing over a list of fruits containing 14
  apples and 11 bananas with a function that takes a bowl and puts a fruit in it and an empty bowl as an initial value
  will return a bowl containing 14 apples and 11 bananas. Got that?

To use a popular piece of advice to writers: "show, don't tell". I've been doing a lot of telling, so now let's take a
look at some actual Java code. You can follow along at home by running the following command from the root of this
repository:

```shell
./gradlew --console plain jshell
```

Or, if you're an IntelliJ user, import this project and then click **Tools > JShell console...**.

### Setting the scene

To make things easier on ourselves, we're going to define three tiny functions:
* `first`: returns the first item in a list
* `rest`: returns all but the first item in a list
* `cons`: puts an item at the beginning of a list. This one may be confusing if you're not a Lisp programmer, but
  apparently "cons" is short for "construct", and `cons` constructs a list by putting two things together, one of which
  is an item and the other of which is a list. Actually, we should probably just stick to saying it puts an item at the
  beginning of a list and forget about trying to justify the odd name. Please don't look up `car` and `cdr`, or you'll
  most likely ragequit this tutorial before we actually get anywhere.

You can type or paste this into JShell, and it will print the result.

First, let's do `first`!

```java
import java.util.List;

<T> T first(List<T> xs) {
    return xs.get(0);
}
```

If you aren't familiar with the Java type system, `<T>` tells Java that we're talking about some type called `T`, then
defining a function that takes a list of `T`s (`List<T>`) and returns a `T`. I know it's not pretty, but if you want
pretty, just head over to Clojure and forget about types altogether! (Mostly.)

Let's test it out!

```java
import java.util.Arrays

first(Arrays.asList(1, 2, 3))
```

`Arrays.asList(1, 2, 3)` is the easiest way to create a list containing `1`, `2`, and `3` in Java.

If everything went well, JShell will print something amazing like:

```
first(Arrays.asList(1, 2, 3))$4 ==> 1
```

The interesting bit comes after the `==>`: that's the result of evaluating the expression that we entered. Here we can
see that calling `first` on the list `1, 2, 3` in fact returns `1`!

Now let's do `rest`:

```java
<T> List<T> rest(List<T> xs) {
    return xs.subList(1, xs.size());
}
```

And test it:

```java
rest(Arrays.asList(1, 2, 3))
```

JShell says:

```
rest(Arrays.asList(1, 2, 3))$8 ==> [2, 3]
```

Cool! Let's move on to `cons`:

```java
import java.util.List;

<T> List<T> cons(T x, List<T> xs) {
    xs.add(0, x);
    return xs;
}
```

And now to test it:

```java
cons(1, Arrays.asList(2, 3))
```

Uh-oh!

```
cons(1, Arrays.asList(2, 3))|  Exception java.lang.UnsupportedOperationException
|        at AbstractList.add (AbstractList.java:153)
|        at cons (#10:2)
|        at (#11:1)
```

Apparently `Arrays.asList` returns an `AbstractList`, which doesn't support the `List.add` method. We can use
`ArrayList` instead:

```java
import java.util.ArrayList;

cons(1, new ArrayList<>(Arrays.asList(2, 3)))
```

By the way, this `new ArrayList<>` incantation is using the "diamond operator", which was added in Java 7. It is
basically telling Java "this is an ArrayList containing objects of some type, now please infer the type so I don't have
to spell it out for you!" In this case, Java can tell that we have a list of integers, so it has the same information
as if we had written `new ArrayList<Integer>`.

JShell says:

```
cons(1, new ArrayList<>(Arrays.asList(2, 3)))$36 ==> [1, 2, 3]
```

It worked!

Having to repeat ourselves with all of the `Arrays.asList` stuff is a little annoying, so let's refactor:

```java
var list1 = new ArrayList<>(Arrays.asList(1, 2, 3))
var list2 = new ArrayList<>(Arrays.asList(2, 3))

first(list1)
rest(list1)
cons(1, list2)
```

Things are still looking pretty good:

```
jshell> first(list1)$39 ==> 1

jshell> rest(list1)$40 ==> [2, 3]

jshell> cons(1, list2)$41 ==> [1, 2, 3]
```

Before we move on, let's think back to the **pure functions** building block. For a function to be pure, it must always
return the same value when called with the same arguments. Let's check this for our three functions:

```
jshell> first(list1)
first(list1)$42 ==> 1

jshell> rest(list1)
rest(list1)$43 ==> [2, 3]

jshell> cons(1, list2)
cons(1, list2)$44 ==> [1, 1, 2, 3]
```

`first` and `rest` are looking good, but what in the world is going on with `cons`? If `list2` is `[2, 3]`, why is
`cons`-ing `1` onto it suddenly `[1, 1, 2, 3]`?

The answer, my friend, is blowing in the wind, and the wind cries "mutability" (it also cries "Mary" according to one
James Marshall Hendrix, Esq., but that's neither here nor there).

The `ArrayList` class implements a mutable data structure, so in addition to returning a bigger list, our `cons`
function actually modifies the list passed in as an argument. The horror!

Actually, this is the reason that using `Arrays.asList` didn't work at first, as an `AbstractList` is immutable, so it
doesn't implement `List.add`. If we have only listened to the compiler instead of believing we knew better!

### Immutability to the rescue

We can fix this pretty easily, it turns out:

```java
<T> List<T> cons(T x, List<T> xs) {
    var ys = new ArrayList<T>(xs);
    ys.add(0, x);
    return ys;
}
```

Let's test:
```java
var list2 = new ArrayList<Integer>(Arrays.asList(2, 3))

cons(1, list2)
cons(1, list2)
list2
```

Quoth JShell:

```
jshell> cons(1, list2)$23 ==> [1, 2, 3]

jshell> cons(1, list2)$24 ==> [1, 2, 3]

jshell> list2 ==> [2, 3]
```

Excellent! We've done it!

Kinda. You may have noticed that the only thing keeping `list2` from being mutated by `cons` is our iron discipline.
Someone else could come along, not knowing that we made an implicit promise to the caller to keep their data immutable.

So let's see what we can do about that.

### Persistent data structures to the rescue

We can redefine `first`, `rest`, and `cons` to actually enforce immutability by switching data structures.

```java
import org.pcollections.PVector;
import org.pcollections.TreePVector;

public static <T> T first(PVector<T> xs) {
    return xs.get(0);
}

public static <T> PVector<T> rest(PVector<T> xs) {
    return xs.subList(1, xs.size());
}

public static <T> PVector<T> cons(T x, PVector<T> xs) {
    return TreePVector.singleton(x).plusAll(xs);
}
```

Before we get into what all of this mumbo-jumbo is, let's see if it even works:

```java
var list1 = TreePVector.from(Arrays.asList(1, 2, 3))
var list2 = TreePVector.from(Arrays.asList(2, 3))

first(list1);
rest(list1);
cons(1, list2);
cons(1, list2);
```

Looking good!

```
jshell> first(list1)$41 ==> 1

jshell> rest(list1)$42 ==> [2, 3]

jshell> cons(1, list2)$43 ==> [1, 2, 3]

jshell> cons(1, list2)$44 ==> [1, 2, 3]
```

OK, now let's hold up for a second and start unpacking things. First of all, what in the world is a [persistent data
structure](https://en.wikipedia.org/wiki/Persistent_data_structure)? Well, it turns out that it has nothing to do with
the type of persistence which usually involves databases and such. A persistent data structure is an immutable data
structure that uses "structural sharing" to remove the need for expensive memory copies like our
`var ys = new ArrayList<T>(xs)` would result in if the list we passed in was really large.

To explain how this works, let's take a closer look at our `cons` function:

```java
public static <T> PVector<T> cons(T x, PVector<T> xs) {
    return TreePVector.singleton(x).plusAll(xs);
}
```

We're using a library here called [PCollections](https://pcollections.org/), which is an implementation of the basic
Java collections as persistent data structures. `PVector` is an interface that extends the Java `List` interface, and
`TreePVector` is a concrete implementation, similar to host `ArrayList` is a concrete implementation of `List`.

Here, `TreePVector.singleton` creates a new list with a single element `x`, which we then append our list of `xs` to
using the `plusAll` method. Unlike its mutable cousin, `List.addAll`, it does not do a memory copy of all the items
in `xs`. It simply creates a new list where the second item is a link to the list `xs`, which is fine because `xs` is
immutable, so it can be shared with any list containing it as a sub-list.

### Back to map, filter, and reduce

Now that we have the ability to take the first item in a list, the rest of the items in a list, and create a new list
by cons-ing an element onto the front of an existing list, we have all we need to implement `map`, `filter`, and
`reduce`.

Let's start with `map`, which if you remember, takes a function `f(x)` and a list of `xs` and returns a new list
where `f` is applied to each item in `xs`. We can define it like this:

```java
import java.util.function.Function;

public static <T, R> PVector<R> map(Function<T, R> f, PVector<T> xs) {
    if (xs.isEmpty()) {
        return TreePVector.empty();
    } else {
        return cons(f.apply(first(xs)), map(f, rest(xs)));
    }
}
```

If we break this down, we're declaring a function that operates on two types:
* `T`: the type of the items in `xs`
* `R`: the type of the items in the new list returned by `map`

`map` returns a `PVector<R>`, meaning basically a list of items of type `R`, and takes as its arguments a function `f`
(which we'll drill into in a moment) and a `PVector<T> xs`, meaning a list of items of type `T`.

If we take a closer look at the type of the function, we see that it's represented in Java by a type `Function<T, R>`.
In Java 1.8, a new package was added called
[java.util.function](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/function/Function.html), which
contains all sorts of useful types for faking first class functions by wrapping them in objects. `Function<T, R>` is
one that models a function taking a single argument of type `T` and returning an item of type `R`. It does this by
providing an instance method `R apply(T t)`. It also has a couple of other instance methods that we'll look at a little
later.

So back to `map`. If we look at the body, we see that the first thing we need to do is check if `xs` is empty. This is
necessary since `map` is a **recursive function**, meaning it calls itself. In order for `map` to not loop endlessly,
we need to make sure to define a **base case**, for which we return something that doesn't call `map`. The standard
pattern for recursive functions that consume a list is to always make the recursive call with a smaller list than you
were called with, and define the empty list as your base case. This is exactly what we've done here: when `xs` is empty,
we simply a new empty list by calling the static function `TreePVector.empty`.

Now that we have our base case, we need to define our **recursive case**, which as we said, needs to recursively call
`map` with a shorter list than it is called with. Our recursive call passes along the same function `f` and the `rest`
of the list `xs` (meaning all but the first item), so we're set here. But since we know that `map` returns a list
containing the same number of items it was called with, we need to also slap something on the front of the list returned
by the recursive call to `map`.

And that's where the call to our function comes in. We call `f` on the first item of `xs`, and then cons it onto the
list returned by the recursive call to `map` with the rest of `xs`. And that's all there is to `map`! If recursion is a
new concept for you, it may take a little thinking about to unpack it.

You can visualise it like this:
* You have a function that turns an apple into a banana
* You have a list of apples
* You call the function on the first apple in your list, giving you a banana
* You add that banana as the first item new list containing only bananas
* Now you repeat the process with the rest of the apples, until you run out of apples

Let's test out our `map` function by calling it with a function `f` that adds 1 to its argument, and our good old list
`[1, 2, 3]`:

```
jshell> map(x -> x + 1, list1);
map(x -> x + 1, list1)$52 ==> [2, 3, 4]
```

There's one last thing to explain before we move on to `filter`, and that's that funky `x -> x + 1` thing. If you're
used to JavaScript or Erlang, you probably already guessed that it's an anonymous function, also known as a **lambda**.
JavaScript uses a fat arrow `=>` instead of a skinny arrow `->`, and Erlang makes anonymous functions fun by starting
them with the keyword `fun` (but then declares the fun over by making you say `end` when the function is done), but
otherwise the syntax is the same.

Anonymous functions in Java containing only a single expression allow you to omit the keyword `return`, as Java knows
you want to return the thing that the expression evaluates to. That makes it really nice to pass around small functions,
and that's why they added the lambda syntax to Java 1.8: to be nice.

### Filtering stuff with filter

`filter` is a function that takes a special kind of function known as a **predicate** (a predicate is a fancy word for
a function that takes an argument and returns `true` or `false`) and a list, and returns a new list containing all the
items for which `p(x)` returns `true`.

We'll define `filter` using our standard pattern for recursive functions:
* If `xs` is empty, return an empty list
* Otherwise, call `filter` recursively with a shorter list

Unlike `map`, we only cons an item onto the result of the recursive call if calling the predicate with the first item
in `xs` returns `true`. If not, we simply return the result of the recursive call.

That will look something like this:

```java
public static <T> PVector<T> filter(Function<T, Boolean> p, PVector<T> xs) {
    if (xs.isEmpty()) {
        return TreePVector.empty();
    } else {
        var x = first(xs);
        return p.apply(x) ? cons(x, filter(p, rest(xs))) : filter(p, rest(xs));
    }
}
```

The `var` keyword was added in Java 10 (and then extended to work with lambdas in Java 11), and it allows you to define
a local variable without stating its type, since Java should have enough information to infer the type (just as it does
when you use the diamond operator, `<>`).

Let's test it by filtering `list1` for just the odd numbers:

```
jshell> filter(x -> x % 2 > 0, list1)
filter(x -> x % 2 > 0, list1)$54 ==> [1, 3]
```

Victory!

### Reduce your stress

`reduce` is unfortunately the hardest function to understand if you haven't seen it before. It takes:
* a function `f` which takes two arguments, an **accumulator** `acc` and an item `x`
* an initial value for the accumulator `acc`
* a list of `xs`

and returns whatever you want, as long as it's the same type as the accumulator.

The recursive pattern here is familiar:
* If `xs` is empty, return the current `acc`
* Otherwise, call `reduce` with the function `f`, a new accumulator value of whatever calling `f` with the current value
  of the accumulator and the first item in `xs` returns, and the rest of `xs`

We can say all of this in Java like so:

```java
import java.util.function.BiFunction;

public static <T, R> R reduce(BiFunction<R, T, R> f, R acc, PVector<T> xs) {
    if (xs.isEmpty()) {
        return acc;
    } else {
        return reduce(f, f.apply(acc, first(xs)), rest(xs));
    }
}
```

The astute reader may have noticed that we've pulled a new type out of our `java.util.function` hat: `BiFunction`.
`BiFunction` is just like `Function`, except it takes two arguments instead of one. If you
[look at the docs](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html), you'll see that
`BiFunction` is defined as `BiFunction<T, U, R>`, where `T` is the type of the first argument to the function, `U` is
the type of the second argument, and `R` is the return value; but in our case, we only need two types, as our reducing
lambda must return the same type as its first argument, hence us defining it as `BiFunction<R, T, R>`.

Let's see if this worked by calling `reduce` with a function that adds the accumulator to the current item:

```
jshell> reduce((acc, x) -> acc + x, 0, list1)
reduce((acc, x) -> acc + x, 0, list1)$61 ==> 6
```

Hurrah!

Before we move on, let's show off one more feature added in Java 8. If we have a function that can be called with the
right number of arguments, we don't need to write an anonymous function at all; we can just pass our function by name.

In this example, there is some function that adds two integers together, and it's called
[`Integer::sum`](https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html#sum-int-int-). Let's swap it in for
our anonymous function:

```
jshell> reduce(Integer::sum, 0, list1)
reduce(Integer::sum, 0, list1)$62 ==> 6
```

Very nice!

### Wagging our tail

One really cool thing about `reduce` is that it's a so-called **tail-recursive** function, meaning that it returns the
return value of the recursive call directly (or the base case), without needing to fiddle with it. Compare the recursive
call in `reduce`:
```java
return reduce(f, f.apply(acc, first(xs)), rest(xs))
```
to the recursive calls in `map` and `filter`:
```java
// map
return cons(f.apply(first(xs)), map(f, rest(xs)))

// filter
return p.apply(x) ? cons(x, filter(p, rest(xs))) : filter(p, rest(xs))
```

You see how both of them return the result of cons-ing something onto the value returned by the recursive call (in at
least one branch of `filter`)? That means that the function needs to hold onto the first item of `xs` in the stack in
order to cons it onto the return value of the recursive call.

It turns out this doesn't matter in Java, as Java lacks something called
[tail call optimisation](https://en.wikipedia.org/wiki/Tail_call) (TCO), wherein the compiler translates a tail call
into standard `for` loop-style iteration. It's pretty neat, but the Java compiler can't do it (yet) because,
[according to Java architect Brian Goetz](https://softwareengineering.stackexchange.com/a/272086),
> in JDK classes [...] there are a number of security sensitive methods that rely on counting stack frames between JDK
> library code and calling code to figure out who's calling them.

This would break if anything changes the number of frames on the stack (such as eliminating frames on recursive calls
through tail call optimisation). The good news is that apparently the JDK no longer relies on counting stack frames for
these methods, so TCO will eventually happen, though it's sadly not a priority.

And why should we care about TCO? Well, in addition to making code run faster by eliminating a bunch of function calls,
TCO lets us call recursive functions on really big lists, which would otherwise cause the JVM to bail with a
`StackOverflowException` when it consumes the maximum allowable number of stack frames, which is limited by the amount
of memory the JVM has been given for the stack (which can be controlled by the `-Xss` command-line argument when
starting the JVM). Before you ask why not just give the JVM a huge amount of memory for the stack, let's understand that
the `StackOverflowException` is actually our friend, as it prevents infinite recursion caused by buggy code from
consuming all available memory on our machine, causing Linux's out of memory killer (or the equivalent on other
operating systems) from killing potentially random programs on our machine until things start swapping and then our
machine slows to a crawl and we can't move our mouse over to the IDE window in order to fix the bug. What a drag!

We have taken this brief detour for a reason, which shall now be revealed.

### Better (but more confusing) map and filter

To prepare ourselves for the glorious day when Java does TCO for us, let's rewrite `map` and `filter` using tail
recursion. Starting with `map`, let's think about what our non-tail recursive approach was:
* If the list of `xs` is empty, return an empty list
* Otherwise, cons `first(xs)` onto the result of mapping `f` over `rest(xs)`

The cool thing about implementing this by using `reduce` is that `reduce` takes care of detecting empty lists and
traversing the list for us, so we can just concentrate on what should happen in the mapping step. Let's give it a
go!

```java
public static <T, R> PVector<R> mapR(Function<T, R> fn, PVector<T> xs) {
    return reduce(
        (PVector<R> acc, T x) -> cons(fn.apply(x), acc),
        TreePVector.empty(),
        xs
    );
}
```

What we're doing here is reducing over `xs` with an empty list as the initial value for the accumulator, and for
each list item, cons-ing the result of `f(x)` onto the accumulator. Sounds pretty good, right? Let's ask JShell:

```
jshell> mapR(x -> x + 1, list1)
mapR(x -> x + 1, list1)$64 ==> [4, 3, 2]
```

So close! The answer is correct, but it's in the wrong order. If we walk through what the `mapR` function is
doing step by step, we can see why:
1. `xs: [1, 2, 3]; acc: []` => `cons(1 + 1, [])`
2. `xs: [2 3]; acc: [2]` => `cons(2 + 1, [2])`
3. `xs: [3]; acc: [3, 2]` => `cons(3 + 1, [3, 2])`
4. `acc: [4, 3, 2]`

What we need to do is to add `f(x)` to the end of `acc` instead of adding it to the beginning. However, we haven't
written a function for this (and don't want to cheat by using `PVector.plus`), so we'll do what Lisp does and just
reverse our result (don't worry, Lisp implementations have highly performant `reverse` functions).

But first, we need to implement `reverse`. And let's do it in a tail recursive fashion, using our best friend
`reduce`!

We can learn from our mistake with `mapR` and realise that reducing with a function that cons-es something onto
and accumulator list reverses it, so all we have to do is take our buggy `mapR` and remove the `f.apply` from the
reducing function:

```java
public static <T> PVector<T> reverse(PVector<T> xs) {
    return reduce((PVector<T> acc, T x) -> cons(x, acc), TreePVector.empty(), xs);
}
```

and prove that it works:

```
jshell> reverse(list1)
reverse(list1)$66 ==> [3, 2, 1]
```

So now we can update `mapR`:

```java
public static <T, R> PVector<R> mapR(Function<T, R> fn, PVector<T> xs) {
    return reverse(
        reduce(
            (PVector<R> acc, T x) -> cons(fn.apply(x), acc),
            TreePVector.empty(),
            xs
        )
    );
}
```

This time, we should be happier with the result:

```
jshell> mapR(x -> x + 1, list1)
mapR(x -> x + 1, list1)$68 ==> [2, 3, 4]
```

With all the knowledge we've accumulated (pun not originally intended but now revelled in), `filterR` should
be straightforward:

```java
public static <T> PVector<T> filterR(Function<T, Boolean> pred, PVector<T> xs) {
    return reverse(
        reduce(
            (PVector<T> acc, T x) -> pred.apply(x) ? cons(x, acc) : acc,
            TreePVector.empty(),
            xs
        )
    );
}
```

And now, for the moment of truth:

```
jshell> filterR(x -> x % 2 > 0, list1)
filterR(x -> x % 2 > 0, list1)$70 ==> [1, 3]
```

### Tying it all together

Now that we understand how `map`, `filter`, and `reduce` work at a very low level, let's solve a problem
using them all in concert:

**What is the sum of the squares of all the odd numbers between 1 and 10?**

If we break this down into its constituent parts, we need to:
1. Take the odd numbers between 1 and 10 (filter)
2. Square each of them (map)
3. Sum them all (reduce)

Saying that in code looks like this:

```java
reduce(
    Integer::sum,
    0,
    map(
        x -> x * x,
        filter(
            x -> x % 2 > 0,
            TreePVector.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        )
    )
)
```

Which JShell tells us evaluates to 165. We'll just take it at its word, to be honest.

We are now functional programmers, and can congratulate ourselves heartily!

But... looking at our code, we come to the following two conclusions:
1. This doesn't look like any Java we've seen
2. This is pretty ugly

The good news is that we can do better, and Java will help us! Let's get into that in the next section.

## Getting idiomatic with it

Let's now move from simple integers into a slightly more realistic domain: money!

Our new problem is fairly straightforward. We need to take a list of prices, and then:
1. Select the ones for which VAT (Value Added Tax) applies
2. Apply VAT to the price
3. Sum up the total amount

Assuming we have the right functions written, that could look something like this:

```java
reduce(Price::sum, 0, map(VAT::applyVAT, filter(price -> VAT.hasVAT(price.currency()), prices)))
```

The main problem with this is that it reads backwards. When we described the problem, we effectively said `filter`,
`map`, then `reduce`, but when we wrote the code, we said `reduce`, `map`, `filter`.

Java 17 gives us some nice tools to turn this around, so let's take a look!

### Naively applying VAT

Let's start by defining a `Price` class to represent a price, which consists of two things: an amount in minor units
(for example, cents, öre, etc.), and an ISO-4217 currency code. We can say this in Java like so:

```java
public class Price {
    private long amount;
    private String currency;

    public Price(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
```

We'll keep up our practice of using JShell to make sure that everything is going well, so how about constructing a
price?

```
jshell> new Price(10000, "SEK")
new Price(10000, "SEK")$4 ==> Price@76fb509a
```

The next thing we need is a way to know if VAT applies to a given price. We'll take the naive approach of assuming that
only European Economic Area countries apply VAT, and that all countries using the Euro have the same VAT rate (they
don't). Let's define a `VAT` class with a `rate` function that returns the VAT rate for a given currency code:

```java
public class VAT {
    public static double rate(String currency) {
        return switch (currency) {
            case "RON" -> 0.19;
            case "BGN", "EUR" -> 0.20;
            case "CZK" -> 0.21;
            case "PLN" -> 0.23;
            case "DKK", "SEK" -> 0.25;
            case "HUF" -> 0.27;
        };
    }
}
```

If you take a closer look at that `switch` statement, you may notice that it's actually a `switch` **expression**,
since it returns a value! This
[was added in Java 14](https://advancedweb.hu/new-language-features-since-java-8-to-17/#switch-expressions) and enables
all sorts of cool stuff, some of which we are using here:
* Since `switch` is now an expression, we can return its result instead of needing to assign to a temporary variable or
  return a value from each branch
* No fall-through, so we don't need explicit `break` statements; instead, we can list multiple constants in a case,
  like we did for Bulgarian Leva and Euros: `case "BGN", "EUR"`
* And as we're about to see, the cases of a `switch` expression are exhaustive

To explain that final point, let's try evaluating our `VAT` class in JShell:

```
   ...>     }|  Error:
|  the switch expression does not cover all possible input values
|                  return switch (currency) {
|                         ^------------------...
```

What we're being told here is that our cases don't cover all the possible values a `String` can have, which makes
sense, since a string can be of any length, and contain any characters. This makes all possible values effectively
infinite, which means that the eight strings covered by our cases will not suffice. We can fix this by adding a default
case:

```java
public class VAT {
    public static double rate(String currency) {
        return switch (currency) {
            case "RON" -> 0.19;
            case "BGN", "EUR" -> 0.20;
            case "CZK" -> 0.21;
            case "PLN" -> 0.23;
            case "DKK", "SEK" -> 0.25;
            case "HUF" -> 0.27;
            default -> 0.0;
        };
    }
}
```

If we paste this into JShell, now we get the report:

```
   ...>     }|  created class VAT
```

Let's give it a try:

```
jshell> VAT.rate("SEK")
VAT.rate("SEK")$8 ==> 0.25

jshell> VAT.rate("EUR")
VAT.rate("EUR")$9 ==> 0.2

jshell> VAT.rate("USD")
VAT.rate("USD")$10 ==> 0.0
```

Looks good!

`switch` gets even more powerful in Java 17 with
[pattern matching](https://advancedweb.hu/new-language-features-since-java-8-to-17/#pattern-matching-for-switch-preview-)!
Compared to Haskell or Erlang, it's slightly more limited since it can't destructure lists and other data structures
(though it looks like that is coming, according to [JEP 405](https://openjdk.java.net/jeps/405)), but it does match on
types and add guards and do simple binding. Read more about it if you're interested. The one caveat is that pattern
matching for `switch` is being previewed in Java 17, so there's a tiny chance it might be changed a little before it's
permanently added in a later version.

We can now use this to add a method to `Price` to apply VAT:

```java
public class Price {
    private long amount;
    private String currency;

    public Price(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Price applyVAT() {
        amount = Math.round(amount * VAT.rate(currency));
        return this;
    }
}
```

We can now play around with this:

```
jshell> var p = new Price(10000, "SEK")
var p = new Price(10000, "SEK")p ==> Price@49c2faae

jshell> var p2 = p.applyVAT()
var p2 = p.applyVAT()p2 ==> Price@49c2faae

jshell> p2.getAmount()
p2.getAmount()$16 ==> 2500
```

Excellent! Let's now apply VAT to a list of prices!

### A better way to map

We could use our existing `map` function to apply VAT to a list of prices:

```
jshell> var prices = TreePVector.from(Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR")))
var prices = TreePVector.from(Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR")))prices ==> [Price@531d72ca, Price@22d8cfe0]

jshell> var pricesWithVAT = map(Price::applyVAT, prices)
var pricesWithVAT = map(Price::applyVAT, prices)pricesWithVAT ==> [Price@531d72ca, Price@22d8cfe0]

jshell> first(pricesWithVAT).getAmount()
first(pricesWithVAT).getAmount()$28 ==> 625
```

But as we complained at the end of the first section, this doesn't look like any Java we've ever seen. Let's see if we
can fix that!

Java 8 introduced something called the [Stream API](https://www.baeldung.com/java-8-streams-introduction), which gives
us a standard `map` function so we don't have to roll our own! It looks like this:

```java
prices.stream().map(Price::applyVAT)
```

The `stream` method was added to the `Collection` interface in Java, which means it is implemented for things like lists
and sets and so on. What it returns is a "pipeline", on which **intermediate operations** (which return a `Stream`) and
**terminal operations** (which return a result of a definite type) can be performed.

`map` is an intermediate operation, meaning that we can perform further operations on our stream. In our case, we want
a new list of prices with VAT applied, so we can use the terminal operation `toList` to accomplish that:

```java
prices.stream().map(Price::applyVAT).toList()
```

Let's get rid of all the persistent data structures, now that we don't really need them to avoid memory copies, and
test this puppy out!

```
jshell> var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))
var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))prices ==> [Price@108c4c35, Price@4ccabbaa]

jshell> var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()
var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()pricesWithVAT ==> [Price@108c4c35, Price@4ccabbaa]

jshell> pricesWithVAT.get(0).getAmount()
pricesWithVAT.get(0).getAmount()$36 ==> 2500
```

Very nice!

We can now write a function that takes a list of prices and applies VAT to them:

```java
List<Price> applyVAT(List<Price> prices) {
    return prices.stream()
            .map(Price::applyVAT)
            .toList();
}
```

Turning to JShell:

```
jshell> var pricesWithVAT = applyVAT(prices)
var pricesWithVAT = applyVAT(prices)pricesWithVAT ==> [Price@4d76f3f8, Price@2d8e6db6]

jshell> pricesWithVAT.get(0).getAmount()
pricesWithVAT.get(0).getAmount()$43 ==> 625
```

This looks good, but something is nagging at us. When we applied VAT to our list of prices with `prices.stream().map()`,
the new price of the first item was 2500, but when we used `applyVAT(prices)`, it was 625. Given that `applyVAT` is
nothing more than taking the code we typed out in JShell and putting it in a function for convenience, what in the
world is going on here?

Let's do some arithmetic ourselves. If the VAT that should be applied to a price in Swedish Kronor (SEK) is 25% and the
price is 100 SEK, the price with VAT applied should be 125 SEK. Let's see if that's true:

```
jshell> var price = new Price(10000, "SEK")
var price = new Price(10000, "SEK")price ==> Price@619a5dff

jshell> var expectedPrice = new Price(12500, "SEK")    
var expectedPrice = new Price(12500, "SEK")expectedPrice ==> Price@497470ed

jshell> var priceWithVAT = price.applyVAT()
var priceWithVAT = price.applyVAT()priceWithVAT ==> Price@619a5dff

jshell> priceWithVAT == expectedPrice
$48 ==> false
```

This is clearly not what we expected. But wait! What does `==` actually do in Java? We have some vague memory that we're
supposed to use `string1.equals(string2)` instead of `string1 == string2`, because what `==` does is tests whether the
two variables have references that point to the same space in memory.

This is not what we want here, because we consider two prices with the same amount and currency to be equal to each
other. We can fix this by overriding the `equals` method for our `Price` class:

```java
public class Price {
    private long amount;
    private String currency;

    public Price(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Price applyVAT() {
        amount = Math.round(amount * VAT.rate(currency));
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Price other) {
            return Objects.equals(this.currency, other.currency) && this.amount == other.amount;
        } else {
            return false;
        }
    }
}
```

Alright, let's see if that fixed it:

```
jshell> var price = new Price(10000, "SEK")
var price = new Price(10000, "SEK")price ==> Price@22927a81

jshell> var expectedPrice = new Price(12500, "SEK")
var expectedPrice = new Price(12500, "SEK")expectedPrice ==> Price@5e8c92f4

jshell> var priceWithVAT = price.applyVAT()
var priceWithVAT = price.applyVAT()priceWithVAT ==> Price@22927a81

jshell> priceWithVAT.equals(expectedPrice)
priceWithVAT.equals(expectedPrice)$57 ==> false
```

No dice, it seems. :(

It's really hard to understand why this is, since JShell is just telling us that `expectedPrice ==> Price@5e8c92f4` and
`priceWithVAT ==> Price@22927a81`. We can definitely improve visibility here by overriding the `toString` method in the
`Price` class:

```java
public class Price {
    private long amount;
    private String currency;

    public Price(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Price applyVAT() {
        amount = Math.round(amount * VAT.rate(currency));
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Price other) {
            return Objects.equals(this.currency, other.currency) && this.amount == other.amount;

        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%d %s", amount, currency);
    }
}
```

Now let's see what we can see:

```
jshell> var price = new Price(10000, "SEK")
var price = new Price(10000, "SEK")price ==> 10000 SEK

jshell> var expectedPrice = new Price(12500, "SEK")
var expectedPrice = new Price(12500, "SEK")expectedPrice ==> 12500 SEK

jshell> var priceWithVAT = price.applyVAT()
var priceWithVAT = price.applyVAT()priceWithVAT ==> 2500 SEK
```

This is **a lot** better! Now we can clearly see that our expected price is 12500 SEK (in minor units, that is) and our
actual price is 2500 SEK.

If we stare at the `Price.applyVAT` method, we can see why:

```java
public Price applyVAT() {
    amount = Math.round(amount * VAT.rate(currency));
    return this;
}
```

Oops! We're setting the amount to the VAT applied, not **adding** the VAT to the current amount. This can be easily
fixed:

```java
public Price applyVAT() {
    amount += Math.round(amount * VAT.rate(currency));
    return this;
}
```

Now things should be better:

```
jshell> var price = new Price(10000, "SEK")
var price = new Price(10000, "SEK")price ==> 10000 SEK

jshell> var expectedPrice = new Price(12500, "SEK")
var expectedPrice = new Price(12500, "SEK")expectedPrice ==> 12500 SEK

jshell> var priceWithVAT = price.applyVAT()
var priceWithVAT = price.applyVAT()priceWithVAT ==> 12500 SEK

jshell> priceWithVAT.equals(expectedPrice)
priceWithVAT.equals(expectedPrice)$67 ==> true
```

Indeed they are!

Now we can return to our mapping:

```
jshell> var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))
var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))prices ==> [10000 SEK, 1000 EUR]

jshell> var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()
var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()pricesWithVAT ==> [12500 SEK, 1200 EUR]

jshell> pricesWithVAT.get(0).getAmount()
pricesWithVAT.get(0).getAmount()$70 ==> 12500
```

Looks great! Now let's see if our `applyVAT` function is working better:

```
jshell> var pricesWithVAT = applyVAT(prices)
var pricesWithVAT = applyVAT(prices)pricesWithVAT ==> [15625 SEK, 1440 EUR]

jshell> pricesWithVAT.get(0).getAmount()
pricesWithVAT.get(0).getAmount()$72 ==> 15625
```

Wait a second here! `applyVAT` is increasing the price somehow! If we go back to staring at `Price.applyVAT`, we can
figure that out, too:

```java
public Price applyVAT() {
    amount += Math.round(amount * VAT.rate(currency));
    return this;
}
```

We forgot that just because our list of prices is immutable when using streams, the individual `Price` objects are not!
And since we're modifying the price in this method, calling `applyVAT` a second time will apply VAT to the price that
has already had VAT applied to it, which hardly seems fair to the poor consumer.

Again, the fix is fairly straightforward. We should create a new `Price` rather than modifying the one we have:

```java
public Price applyVAT() {
    return new Price(amount + Math.round(amount * VAT.rate(currency)), currency);
}
```

If we paste the new version of the class into JShell, we can then see if our fix worked:

```
jshell> var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))
var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))prices ==> [10000 SEK, 1000 EUR]

jshell> var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()
var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()pricesWithVAT ==> [12500 SEK, 1200 EUR]

jshell> pricesWithVAT.get(0)
pricesWithVAT.get(0)$76 ==> 12500 SEK

jshell> applyVAT(prices).get(0)
applyVAT(prices).get(0)$77 ==> 12500 SEK
```

Victory is ours, surprisingly!

For developers used to Erlang or Haskell or Scala or Clojure, there's still an annoyance here, though: it takes 40 lines
of code to represent a price, and then you have to be careful about how you implement methods to ensure that objects are
immutable!

### Records to the rescue

Java 16 fixes this problem with something called
[Record Classes](https://advancedweb.hu/new-language-features-since-java-8-to-17/#record-classes), which are similar to
case classes in Scala and records in Erlang. They are intended to be transparent carriers of shallowly immutable data.

We can replace all of our `Price` class with a single line:

```java
public record Price(long amount, String currency) { }
```

This lets us do all sorts of neat stuff:

```
jshell> var price = new Price(10000, "SEK")
var price = new Price(10000, "SEK")price ==> Price[amount=10000, currency=SEK]

jshell> price.amount()
price.amount()$80 ==> 10000

jshell> price.currency()
price.currency()$81 ==> "SEK"

jshell> var price2 = new Price(10000, "SEK")
var price2 = new Price(10000, "SEK")price2 ==> Price[amount=10000, currency=SEK]

jshell> price.equals(price2)
price.equals(price2)$83 ==> true
```

So a record class gives us all the following without having to write any code:
* A constructor which initialises all fields
* Getters for all fields (without the annoying `get` prefix)
* A `toString` override that shows us the values of the fields
* An `equals` override that returns true if two objects are both instances of the record class and all their fields
  have the same values

Of course, we've lost one piece of functionality: our `applyVAT` method. No worries! Since a record class is also a
class, we can add methods to it just like any other class:

```java
public record Price(long amount, String currency) {
    public Price applyVAT() {
        return new Price(amount + Math.round(amount * VAT.rate(currency)), currency);
    }
}
```

Checking in with JShell proves that all is well:

```
jshell> var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))
var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"))prices ==> [Price[amount=10000, currency=SEK], Price[amount=1000, currency=EUR]]

jshell> var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()
var pricesWithVAT = prices.stream().map(Price::applyVAT).toList()pricesWithVAT ==> [Price[amount=12500, currency=SEK], Price[amount=1200, currency=EUR]]

jshell> pricesWithVAT.get(0)
pricesWithVAT.get(0)$88 ==> Price[amount=12500, currency=SEK]

jshell> applyVAT(prices).get(0)
applyVAT(prices).get(0)$89 ==> Price[amount=12500, currency=SEK]
```

### Solving the problem

As per our original problem statement, we're actually trying to sum up all the prices for which VAT applies, meaning
that we need to discard non-EEA prices. We can do this with the `filter` intermediate stream operation:

```java
prices.stream().filter(price -> VAT.rate(price.currency()) > 0).toList()
```

Let's see it in action:

```
jshell> var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"), new Price(1000, "USD"))
var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"), new Price(1000, "USD"))prices ==> [Price[amount=10000, currency=SEK], Price[amount= ... mount=1000, currency=USD]]

jshell> prices.stream().filter(price -> VAT.rate(price.currency()) > 0).toList()
prices.stream().filter(price -> VAT.rate(price.currency()) > 0).toList()$94 ==> [Price[amount=10000, currency=SEK], Price[amount=1000, currency=EUR]]
```

This looks perfect! Prices in US dollars shouldn't have VAT applied, and sure enough, the USD price is omitted from the
filtered list.

We can even make this tidier by adding a tiny helper method to `Price`:

```java
public record Price(long amount, String currency) {
    public Price applyVAT() {
        return new Price(amount + Math.round(amount * VAT.rate(currency)), currency);
    }
    
    public boolean hasVAT() {
        return VAT.rate(currency) > 0;
    }
}
```

This looks a lot nicer:

```
jshell> var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"), new Price(1000, "USD"))
var prices = Arrays.asList(new Price(10000, "SEK"), new Price(1000, "EUR"), new Price(1000, "USD"))prices ==> [Price[amount=10000, currency=SEK], Price[amount= ... mount=1000, currency=USD]]

jshell> prices.stream().filter(Price::hasVAT).toList()
prices.stream().filter(Price::hasVAT).toList()$103 ==> [Price[amount=10000, currency=SEK], Price[amount=1000, currency=EUR]]
```

To sum things up, we can just take what we know about `reduce` and guess how to do it with streams:

```java
prices.stream()
    .filter(Price::hasVAT)
    .map(Price::amount)
    .reduce(0, (acc, amount) -> acc + amount)
```

Let's see if this works in JShell:

```
jshell> prices.stream().filter(Price::hasVAT).map(Price::amount).reduce(0L, (acc, amount) -> acc + amount)
prices.stream().filter(Price::hasVAT).map(Price::amount).reduce(0L, (acc, amount) -> acc + amount)$104 ==> 11000
```

We can also use a function reference rather than a lambda for the reducer:

```
jshell> prices.stream().filter(Price::hasVAT).map(Price::amount).reduce(0L, Long::sum)
prices.stream().filter(Price::hasVAT).map(Price::amount).reduce(0L, Long::sum)$105 ==> 11000
```

It's a little annoying that we have to provide an initial value for `reduce`, since the identity for addition is well
understood, and also annoying that we have to write it as `0L` so that Java can understand the types; if we just write
`0`, we get the following explosion:

```
prices.stream().filter(Price::hasVAT).map(Price::amount).reduce(0, Long::sum)|  Error:
|  no suitable method found for reduce(int,Long::sum)
|      method java.util.stream.Stream.reduce(java.lang.Long,java.util.function.BinaryOperator<java.lang.Long>) is not applicable
|        (argument mismatch; int cannot be converted to java.lang.Long)
|      method java.util.stream.Stream.<U>reduce(U,java.util.function.BiFunction<U,? super java.lang.Long,U>,java.util.function.BinaryOperator<U>) is not applicable
|        (cannot infer type-variable(s) U
|          (actual and formal argument lists differ in length))
|  prices.stream().filter(price -> VAT.rate(price.currency()) > 0).map(Price::amount).reduce(0, Long::sum)
|  ^---------------------------------------------------------------------------------------^
```

It's also annoying that we even need `map` here. We could use our version of `reduce` like this:

```java
reduce((Long acc, Price price) -> acc + price.amount(), 0, prices)
```

But `Stream.reduce` requires the reducing function to be a
[`BinaryOperator<T>`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/BinaryOperator.html),
which takes two arguments of type `T`, meaning that we first need to map over our filtered prices to get their amount,
and **then** sum them up.

Surely we can do better, right?

Indeed we can!

### Enter collectors

Streams have another cool terminal operation called `collect`. `collect` basically combines `map` and `reduce`, and
the Java standard library has loads of built-in
[Collectors](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Collectors.html) that we can
use for common operations. For example, to solve the problem at hand:

```java
prices.stream()
    .filter(Price::hasVAT)
    .collect(Collectors.summingLong(Price::amount)
```

Let's try it out:

```
jshell> prices.stream().filter(Price::hasVAT).collect(Collectors.summingLong(Price::amount))
prices.stream().filter(Price::hasVAT).collect(Collectors.summingLong(Price::amount))$100 ==> 11000
```

Collectors can be combined in cool ways as well. In fact, there is one thing about our "solution" to this problem that
has been setting off the old Spidey Sense: we're summing up prices of different currencies, which sounds like a recipe
for disaster. Let's fix this by summing up prices of the same currency:

```
var prices = Arrays.asList(
    new Price(10000, "SEK"),
    new Price(1000, "EUR"),
    new Price(1000, "USD"),
    new Price(15000, "SEK"),
    new Price(32000, "SEK"),
    new Price(20000, "EUR")
)

prices.stream()
    .filter(Price::hasVAT)
    .collect(Collectors.groupingBy(Price::currency, Collectors.summingLong(Price::amount))
```

Let's see what this does:

```
jshell> prices.stream().filter(Price::hasVAT).collect(Collectors.groupingBy(Price::currency, Collectors.summingLong(Price::amount)))
prices.stream().filter(Price::hasVAT).collect(Collectors.groupingBy(Price::currency, Collectors.summingLong(Price::amount)))$108 ==> {EUR=21000, SEK=57000}
```

OK, **that** is super cool! And we accomplished it in one compact expression, using higher order functions, operating
on immutable data.

My friends, we have just done some real functional programming in Java!
