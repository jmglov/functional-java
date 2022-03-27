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

```
import java.util.List

<T> T first(List<T> xs) {
    return xs.get(0);
}
```

If you aren't familiar with the Java type system, `<T>` tells Java that we're talking about some type called `T`, then
defining a function that takes a list of `T`s (`List<T>`) and returns a `T`. I know it's not pretty, but if you want
pretty, just head over to Clojure and forget about types altogether! (Mostly.)

Let's test it out!

```
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

```
<T> List<T> rest(List<T> xs) {
    return xs.subList(1, xs.size());
}
```

And test it:

```
rest(Arrays.asList(1, 2, 3))
```

JShell says:

```
rest(Arrays.asList(1, 2, 3))$8 ==> [2, 3]
```

Cool! Let's move on to `cons`:

```
import java.util.List;

<T> List<T> cons(T x, List<T> xs) {
    xs.add(0, x);
    return xs;
}
```

And now to test it:

```
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

```
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

```
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

```
<T> List<T> cons(T x, List<T> xs) {
    var ys = new ArrayList<T>(xs);
    ys.add(0, x);
    return ys;
}
```

Let's test:
```
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

```
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

```
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

```
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

```
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

```
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

```
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
lambda must return the same type as its first argument, hence us defining it as `BiFunction<R, R, R>`.

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
```
return reduce(f, f.apply(acc, first(xs)), rest(xs))
```
to the recursive calls in `map` and `filter`:
```
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

### Better (but more confusing) map and reduce

To prepare ourselves for the glorious day when Java does TCO for us, let's rewrite `map` and `filter` using tail
recursion. Starting with `map`, let's think about what our non-tail recursive approach was:
* If the list of `xs` is empty, return an empty list
* Otherwise, cons `first(xs)` onto the result of mapping `f` over `rest(xs)`

The cool thing about implementing this by using `reduce` is that `reduce` takes care of detecting empty lists and
traversing the list for us, so we can just concentrate on what should happen in the mapping step. Let's give it a
go!

```
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

```
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

```
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

```
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

```
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

