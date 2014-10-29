package org.cdlflex.fruit;

/**
 * An expression operator.
 */
public enum Operator {
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<="),
    EQ("="),
    LIKE("LIKE"),
    BETWEEN("BETWEEN"),
    IN("IN");

    private final String symbol;

    private Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the operator for the given symbol. E.g. "=" will yield {@link Operator#EQ}.
     *
     * @param symbol the operator symbol
     * @return the operator
     * @throws IllegalArgumentException on non-existing symbols
     */
    public static Operator forSymbol(String symbol) throws IllegalArgumentException {
        for (Operator operator : values()) {
            if (symbol.equals(operator.getSymbol())) {
                return operator;
            }
        }
        throw new IllegalArgumentException("No operator with symbol " + symbol);
    }
}
