/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.google.common.base.Preconditions;
import com.workday.autoparse.xml.context.XmlContextHolder;

import javax.xml.stream.XMLStreamConstants;

/**
 * A utility class used by the {@link XmlElementParser}s. Clients should not need to use this
 * class.
 *
 * @author nathan.taylor
 * @since 2013-9-19-17:58
 */
public class ParserUtils {

    private ParserUtils() {
    }

    /**
     * Assuming the reader is at a {@link XMLStreamConstants#START_ELEMENT} event, this method finds
     * the {@link XmlElementParser} associated with the element's name and calls {@link
     * XmlElementParser#parseElement (javax.xml.stream.XMLStreamReader)} on it.
     *
     * @param reader The XMLStreamReader to operate on.
     *
     * @return A fully inflated object mapped to the current element's name.
     *
     * @throws UnknownElementException if there is no mapping from the current element's name to an
     * XmlElementParser.
     * @throws IllegalStateException if the {@code reader} is not at a START_ELEMENT.
     */
    public static Object parseCurrentElement(XmlStreamReader reader)
            throws UnknownElementException, UnexpectedChildException, IllegalStateException,
            ParseException {
        Preconditions.checkState(reader.isStartElement(), "The reader must be at a start element.");
        String name = reader.getName();
        XmlElementParser<?> parser = XmlContextHolder.getContext().getParserMap().get(name);
        if (parser == null) {
            parser = UnexpectedElementHandler.handleUnknownElement(reader);
        }

        if (parser != null) {
            return parser.parseElement(reader);
        }
        return null;
    }

    /**
     * Skips the current element and all descendants. When complete, the {@code reader} will be at
     * the event immediately following the current element's END_ELEMENT. If the reader starts at an
     * {@link XMLStreamConstants#END_ELEMENT}, this method will simply move to the next event and
     * return.
     *
     * @param reader The {@link XmlStreamReader} on which to operate.
     */
    public static void skipElement(XmlStreamReader reader)
            throws ParseException {

        int depth = (reader.isEndElement()) ? 0 : 1;

        while (depth > 0) {
            reader.next();
            if (reader.isStartElement()) {
                depth++;
            } else if (reader.isEndElement()) {
                depth--;
            }
        }
        reader.next();
        if (reader.isCharacters()) {
            reader.nextTag();
        }
    }

}
