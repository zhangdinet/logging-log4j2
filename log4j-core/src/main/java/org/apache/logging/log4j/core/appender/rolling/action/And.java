/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * Composite {@code PathCondition} that only accepts objects that are accepted by <em>all</em> component filters.
 */
@Plugin(name = "And", category = "Core", printObject = true)
public final class And implements PathCondition {

    private final PathCondition[] components;

    private And(final PathCondition... filters) {
        this.components = Objects.requireNonNull(filters, "filters");
    }

    public PathCondition[] getDeleteFilters() {
        return components;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.logging.log4j.core.appender.rolling.action.DeleteFilter#accept(java.nio.file.Path,
     * java.nio.file.Path)
     */
    @Override
    public boolean accept(final Path baseDir, final Path relativePath, final BasicFileAttributes attrs) {
        for (final PathCondition component : components) {
            if (!component.accept(baseDir, relativePath, attrs)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a Composite PathCondition that all need to accept before this condition accepts.
     * 
     * @param components The component filters.
     * @return A Composite PathCondition.
     */
    @PluginFactory
    public static And createAndCondition( //
            @PluginElement("PathConditions") final PathCondition... components) {
        return new And(components);
    }

    @Override
    public String toString() {
        return "And" + Arrays.toString(components);
    }
}