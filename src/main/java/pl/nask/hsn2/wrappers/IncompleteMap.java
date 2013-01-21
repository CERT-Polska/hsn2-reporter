/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.wrappers;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class IncompleteMap<K, V> implements Map<K, V> {

    @Override
    public int size() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public V get(Object key) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("not implemented");
    }

}
