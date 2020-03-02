/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.atlas.pc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StatusReporter<T, U> {
    private Map<T,U> producedItems = new LinkedHashMap<>();
    private Set<T> processedSet = new HashSet<>();

    public void produced(T item, U index) {
        this.producedItems.put(item, index);
    }

    public void processed(T item) {
        this.processedSet.add(item);
    }

    public void processed(T[] index) {
        this.processedSet.addAll(Arrays.asList(index));
    }

    public U ack() {
        U ack = null;
        U ret;
        do {
            ret = completionIndex(getFirstElement(this.producedItems));
            if (ret != null) {
                ack = ret;
            }
        } while(ret != null);

        return ack;
    }

    private Map.Entry<T, U> getFirstElement(Map<T, U> map) {
        if (map.isEmpty()) {
            return null;
        }

        return map.entrySet().iterator().next();
    }

    private U completionIndex(Map.Entry<T, U> lookFor) {
        U ack = null;
        if (lookFor == null || !processedSet.contains(lookFor.getKey())) {
            return ack;
        }

        ack = lookFor.getValue();
        producedItems.remove(lookFor.getKey());
        processedSet.remove(lookFor);
        return ack;
    }
}
