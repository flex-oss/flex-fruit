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

import java.io.Serializable;

/**
 * A filter Predicate.
 */
public class Predicate implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private Operator op;
    private Object value;

    private boolean not;

    public Predicate(String key, Operator op, Object value) {
        this.key = key;
        this.op = op;
        this.value = value;
    }

    public Predicate(String key, Object value) {
        this(key, Operator.EQ, value);
    }

    public Predicate(String key, String op, Object value) {
        this(key, Operator.forSymbol(op), value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Operator getOp() {
        return op;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    /**
     * Negates the predicate.
     * 
     * @return this for chaining.
     */
    public Predicate not() {
        setNot(true);
        return this;
    }

}
