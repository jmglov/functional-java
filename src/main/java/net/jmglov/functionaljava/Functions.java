package net.jmglov.functionaljava;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Functions {
    public static <T> PVector<T> cons(T x, PVector<T> xs) {
        return TreePVector.singleton(x).plusAll(xs);
    }

    public static <T> T first(PVector<T> xs) {
        return xs.get(0);
    }

    public static <T> PVector<T> rest(PVector<T> xs) {
        return xs.subList(1, xs.size());
    }

    public static <T, R> PVector<R> map(Function<T, R> fn, PVector<T> xs) {
        if (xs.isEmpty()) {
            return TreePVector.empty();
        } else {
            return cons(fn.apply(first(xs)), map(fn, rest(xs)));
        }
    }

    public static <T> PVector<T> filter(Function<T, Boolean> pred, PVector<T> xs) {
        if (xs.isEmpty()) {
            return TreePVector.empty();
        }

        var x = first(xs);
        return pred.apply(x) ? cons(x, filter(pred, rest(xs))) : filter(pred, rest(xs));
    }

    public static <T, R> R reduce(BiFunction<R, T, R> fn, R acc, PVector<T> xs) {
        if (xs.isEmpty()) {
            return acc;
        } else {
            return reduce(fn, fn.apply(acc, first(xs)), rest(xs));
        }
    }

    public static <T> PVector<T> reverse(PVector<T> xs) {
        return reduce((PVector<T> acc, T x) -> cons(x, acc), TreePVector.empty(), xs);
    }

    public static <T, R> PVector<R> mapR(Function<T, R> fn, PVector<T> xs) {
        return reverse(
                reduce((PVector<R> acc, T x) -> cons(fn.apply(x), acc), TreePVector.empty(), xs)
        );
    }

    public static <T> PVector<T> filterR(Function<T, Boolean> pred, PVector<T> xs) {
        return reverse(
                reduce(
                        (PVector<T> acc, T x) -> pred.apply(x) ? cons(x, acc) : acc,
                        TreePVector.empty(),
                        xs
                )
        );
    }
}
