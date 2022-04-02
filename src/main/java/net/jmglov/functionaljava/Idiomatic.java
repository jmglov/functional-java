package net.jmglov.functionaljava;

import java.util.List;

public class Idiomatic {
    public static List<Price> applyVAT(List<Price> prices) {
        return prices.stream()
                .map(Price::applyVAT)
                .toList();
    }

    public static List<PriceR> applyVATR(List<PriceR> prices) {
        return prices.stream()
                .map(PriceR::applyVAT)
                .toList();
    }
}
