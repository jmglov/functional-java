package net.jmglov.functionaljava;

import org.junit.jupiter.api.Test;
import org.pcollections.TreePVector;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static net.jmglov.functionaljava.Functions.*;

class FunctionsTest {

    @Test
    void testCons() {
        assertEquals(
                TreePVector.from(Arrays.asList(1, 2, 3)),
                cons(1, TreePVector.from(Arrays.asList(2, 3)))
        );
    }

    @Test
    void testFirst() {
        assertEquals(1, first(TreePVector.from(Arrays.asList(1, 2, 3))));
    }

    @Test
    void testRest() {
        assertEquals(
                TreePVector.from(Arrays.asList(2, 3)),
                rest(TreePVector.from(Arrays.asList(1, 2, 3)))
        );
    }

    @Test
    void testMap() {
        assertEquals(
                TreePVector.from(Arrays.asList(2, 3, 4)),
                map(x -> x + 1, TreePVector.from(Arrays.asList(1, 2, 3)))
        );
    }

    @Test
    void testFilter() {
        assertEquals(
                TreePVector.from(Arrays.asList(1, 3)),
                filter(x -> x % 2 != 0, TreePVector.from(Arrays.asList(1, 2, 3)))
        );
    }

    @Test
    void testReduce() {
        assertEquals(
                6,
                reduce(Integer::sum, 0, TreePVector.from(Arrays.asList(1, 2, 3)))
        );
    }

    @Test
    void testReverse() {
        assertEquals(
                TreePVector.from(Arrays.asList(1, 2, 3)),
                reverse(TreePVector.from(Arrays.asList(3, 2, 1)))
        );
    }

    @Test
    void testMapR() {
        assertEquals(
                TreePVector.from(Arrays.asList(2, 3, 4)),
                mapR(x -> x + 1, TreePVector.from(Arrays.asList(1, 2, 3)))
        );
    }

    @Test
    void testFilterR() {
        assertEquals(
                TreePVector.from(Arrays.asList(1, 3)),
                filterR(x -> x % 2 != 0, TreePVector.from(Arrays.asList(1, 2, 3)))
        );
    }
}
