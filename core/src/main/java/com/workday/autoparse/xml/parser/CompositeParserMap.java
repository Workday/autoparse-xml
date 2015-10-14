/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author nathan.taylor
 * @since 2015-03-02
 */
public class CompositeParserMap implements ParserMap {

    private final Collection<ParserMap> components;

    public CompositeParserMap(Collection<ParserMap> components) {
        this.components = Collections.unmodifiableCollection(new ArrayList<>(components));
    }

    @Override
    public XmlElementParser<?> get(String name) {
        for (ParserMap parserMap : components) {
            XmlElementParser<?> parser = parserMap.get(name);
            if (parser != null) {
                return parser;
            }
        }
        return null;
    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        for (ParserMap component : components) {
            keySet.addAll(component.keySet());
        }
        return keySet;
    }
}
