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
import java.util.ArrayList;
import java.util.List;

/**
 * A Simple where clause that contains a set of predicates connected with a given connective.
 */
public class Filter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Connective connective;
    private List<Predicate> predicates;

    public Filter() {
        this(new ArrayList<Predicate>());
    }

    public Filter(Predicate predicate) {
        this();
        add(predicate);
    }

    public Filter(Connective connective) {
        this(connective, new ArrayList<Predicate>());
    }

    public Filter(List<Predicate> predicates) {
        this(Connective.AND, predicates);
    }

    public Filter(Connective connective, List<Predicate> predicates) {
        this.connective = connective;
        this.predicates = predicates;
    }

    /**
     * Adds the given Predicate to the list of predicates.
     * 
     * @param attribute the attribute
     * @param op the operator
     * @param value the value
     * @return this for chaining.
     * @see Predicate(String, Operator, Object)
     */
    public Filter add(String attribute, Operator op, Object value) {
        return add(new Predicate(attribute, op, value));
    }

    /**
     * Adds the given Predicate to the list of predicates.
     * 
     * @param predicate the predicate
     * @return this for chaining
     */
    public Filter add(Predicate predicate) {
        getPredicates().add(predicate);
        return this;
    }

    public Connective getConnective() {
        return connective;
    }

    public void setConnective(Connective connective) {
        this.connective = connective;
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<Predicate> predicates) {
        this.predicates = predicates;
    }
}
