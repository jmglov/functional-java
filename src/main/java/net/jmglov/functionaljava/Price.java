package net.jmglov.functionaljava;

import java.util.Objects;

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
        return new Price(amount + Math.round(amount * VAT.rate(currency)), currency);
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
