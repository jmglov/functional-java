package net.jmglov.functionaljava;

public record PriceR(long amount, String currency) {
    public PriceR applyVAT() {
        return new PriceR(amount + Math.round(amount * VAT.rate(currency)), currency);
    }

    public boolean hasVAT() {
        return VAT.rate(currency) > 0;
    }
}
