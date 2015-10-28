/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.context.XmlParserSettings;
import com.workday.autoparse.xml.context.XmlParserSettingsBuilder;

import java.io.InputStream;

/**
 * @author nathan.taylor
 * @since 2013-10-4
 */
public interface XmlStreamParser {
    /**
     * Parses an input stream into objects annotated with {@link XmlElement}.
     *
     * @param in The input stream.
     *
     * @return The fully inflated object corresponding to the root element of the input stream.
     *
     * @throws ParseException If there was an error processing the stream, usually if the content is
     * malformed.
     * @throws UnknownElementException If there was an element with an unmapped name. See {@link
     * XmlParserSettingsBuilder#withUnknownElementHandling(XmlParserSettings.UnknownElementHandling)
     * }.
     * @throws UnexpectedChildException If an element had an undeclared child. See {@link
     * XmlParserSettingsBuilder#ignoreUnexpectedChildren(boolean)}.
     */
    Object parseStream(InputStream in)
            throws ParseException, UnknownElementException, UnexpectedChildException;
}
