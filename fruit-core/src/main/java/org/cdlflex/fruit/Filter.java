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
