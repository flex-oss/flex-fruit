/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
