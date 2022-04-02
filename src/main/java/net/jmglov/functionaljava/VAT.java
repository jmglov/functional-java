package net.jmglov.functionaljava;

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
