package org.cdlflex.fruit;

import java.io.Serializable;

/**
 * Represents objects that can be uniquely identified by a key.
 * 
 * @param <K> The key type
 */
public interface Identifiable<K> extends Serializable {
    /**
     * Gets the current key.
     * 
     * @return the key
     */
    K getId();

    /**
     * Sets the key.
     * 
     * @param id the key
     */
    void setId(K id);
}
