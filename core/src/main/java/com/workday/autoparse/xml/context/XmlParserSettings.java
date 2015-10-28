/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.context;

import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlUnknownElement;
import com.workday.autoparse.xml.parser.UnknownElementException;
import com.workday.autoparse.xml.parser.XmlElementParser;
import com.workday.autoparse.xml.parser.XmlStreamParser;
import com.workday.autoparse.xml.parser.XmlStreamParserFactory;
import com.workday.autoparse.xml.utils.Preconditions;
import com.workday.autoparse.xml.utils.StringTransformer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A collection of preferences that can be set when creating a new {@link XmlStreamParser}.
 *
 * @author nathan.taylor
 * @see XmlParserSettingsBuilder
 * @see XmlStreamParserFactory#newXmlStreamParser(XmlParserSettings)
 * @since 2013-9-18- 19:08
 */
public class XmlParserSettings {

    /**
     * Represents strategies for how autoparse should handle xml elements that are not mapped to a
     * class via @{@link XmlElement}.
     */
    public enum UnknownElementHandling {
        /**
         * Throw an {@link UnknownElementException} if an unmapped xml element is encountered, even
         * if there is a class annotated with {@link XmlUnknownElement}.
         */
        ERROR,
        /**
         * Ignore any unmapped xml elements and do not attempt to parse them, even if there is a
         * class annotated with {@link XmlUnknownElement}.
         */
        IGNORE,
        /**
         * Parse unknown elements into instance of the class annotated with {@link
         * XmlUnknownElement}. If there is no such class, this behaves like {@link #ERROR}.
         */
        PARSE
    }

    private final UnknownElementHandling unknownElementHandling;
    private final boolean ignoreUnexpectedChildren;
    private final List<StringTransformer> stringTransformers;
    private final Class<?> unknownElementClass;
    private final XmlElementParser<?> unknownElementParser;
    private final Collection<String> partitionPackages;

    XmlParserSettings(UnknownElementHandling unknownElementHandling,
                      boolean ignoreUnexpectedChildren,
                      Collection<String> partitionPackages) {
        this(unknownElementHandling, ignoreUnexpectedChildren, null, null,
             Collections.<StringTransformer>emptyList(), partitionPackages);
    }

    XmlParserSettings(UnknownElementHandling unknownElementHandling,
                      boolean ignoreUnexpectedChildren,
                      Class<?> unknownElementClass,
                      XmlElementParser<?> unknownElementParser,
                      List<StringTransformer> stringTransformers,
                      Collection<String> partitionPackages) {
        Preconditions.checkArgument(partitionPackages.size() > 0,
                                    "You must declare at least one partition package.");

        this.unknownElementHandling = unknownElementHandling;
        this.ignoreUnexpectedChildren = ignoreUnexpectedChildren;
        this.unknownElementClass = unknownElementClass;
        this.unknownElementParser = unknownElementParser;
        this.stringTransformers = stringTransformers;
        this.partitionPackages = partitionPackages;
    }

    public boolean ignoresUnexpectedChildren() {
        return ignoreUnexpectedChildren;
    }

    public UnknownElementHandling getUnknownElementHandling() {
        return unknownElementHandling;
    }

    public Class<?> getUnknownElementClass() {
        return unknownElementClass;
    }

    public XmlElementParser<?> getUnknownElementParser() {
        return unknownElementParser;
    }

    public List<StringTransformer> getStringTransformers() {
        return stringTransformers;
    }

    Collection<String> getPartitionPackages() {
        return partitionPackages;
    }
}