package net.jmglov.functionaljava;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IdiomaticTest {

    @Test
    void applyVAT() {
        var originalPrices = Arrays.asList(
                new Price(10000, "SEK"),
                new Price(1000, "EUR")
        );
        var expected = Arrays.asList(
                new Price(12500, "SEK"),
                new Price(1200, "EUR")
        );

        assertIterableEquals(expected, Idiomatic.applyVAT(originalPrices));
    }

    @Test
    void applyVATR() {
        var originalPrices = Arrays.asList(
                new PriceR(10000, "SEK"),
                new PriceR(1000, "EUR")
        );
        var expected = Arrays.asList(
                new PriceR(12500, "SEK"),
                new PriceR(1200, "EUR")
        );

        originalPrices.stream()
                        .filter(p -> VAT.rate(p.currency()) > 0)
                        .collect(Collectors.summingLong(PriceR::amount));
        assertIterableEquals(expected, Idiomatic.applyVATR(originalPrices));
    }

}