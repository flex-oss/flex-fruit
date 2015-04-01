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
 * A range object.
 * 
 * @param <T> the value type
 */
public class Range<T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T start;
    private T end;

    public Range() {
    }

    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    /**
     * Checks whether the given value is included in this range. Checking for 1 in the range of 1,10 will return true by
     * default.
     * 
     * @param value the value to check
     * @return true if the value is inside this range, false otherwise
     */
    public boolean includes(T value) {
        return includes(value, true);
    }

    /**
     * Checks whether the given value is included in this range. If inclusive is set to true, checking for 1 in the
     * range of 1,10 will return true.
     * 
     * @param value the value to check
     * @param inclusive whether or not the range is open (the value is included)
     * @return true if the value is inside this range, false otherwise
     */
    public boolean includes(T value, boolean inclusive) {
        if (inclusive) {
            return value.compareTo(getStart()) >= 0 && value.compareTo(getEnd()) <= 0;
        } else {
            return value.compareTo(getStart()) > 0 && value.compareTo(getEnd()) < 0;
        }

    }

}
