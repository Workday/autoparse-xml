/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.context.XmlContextHolder;
import com.workday.autoparse.xml.context.XmlParserSettings;
import com.workday.autoparse.xml.utils.Preconditions;

/**
 * Utiltiy class for handling both unexpected and unknown elements depending on the current {@link
 * XmlStreamParser}'s {@link XmlParserSettings}. This class should not be used by clients.
 *
 * @author nathan.taylor
 * @since 2013-9-18
 */
public class UnexpectedElementHandler {

    private UnexpectedElementHandler() {
    }

    /**
     * Skips the current element, throws an exception, or returns an unknown element {@link
     * XmlElementParser} depending on the current autoparse's Settings. The reader must be at a
     * {@link javax.xml.stream.XMLStreamConstants#START_ELEMENT} event.
     *
     * @param reader The XMLStreamReader on which to operate.
     *
     * @return The parser for unknown elements if the settings indicate that it should be used,
     * otherwise null.
     *
     * @throws UnknownElementException if the unknown element handling strategy is {@link
     * XmlParserSettings.UnknownElementHandling#ERROR} or if the strategy is {@link
     * XmlParserSettings.UnknownElementHandling#PARSE} and there is no class annotated with {@link
     * com.workday.autoparse.xml.annotations.XmlUnknownElement}.
     */
    public static XmlElementParser<?> handleUnknownElement(
            XmlStreamReader reader)
            throws UnknownElementException, ParseException {

        Preconditions.checkArgument(reader.isStartElement(), "Must be at a start element event.");

        XmlElementParser<?> parserForUnknownElements = XmlContextHolder.getContext()
                                                                       .getSettings()
                                                                       .getUnknownElementParser();

        XmlParserSettings.UnknownElementHandling unknownElementHandling =
                XmlContextHolder.getContext()
                                .getSettings()
                                .getUnknownElementHandling();

        if (unknownElementHandling == XmlParserSettings.UnknownElementHandling.IGNORE) {
            ParserUtils.skipElement(reader);
            return null;
        } else if (unknownElementHandling == XmlParserSettings.UnknownElementHandling.PARSE
                && parserForUnknownElements != null) {
            return parserForUnknownElements;
        } else {
            throw new UnknownElementException(reader.getName());
        }
    }

    /**
     * Throws an UnexpectedChildException if the current autoparse's Settings dictate such.
     * Otherwise, this method does nothing.
     *
     * @param parent The parent object of the unexpected child.
     * @param child The resulting child object of the unexpected XML element.
     * @param childName The name of the unexpected XML element.
     */
    public static void handleUnexpectedChild(Object parent, Object child, String childName)
            throws UnexpectedChildException {

        if (!XmlContextHolder.getContext().getSettings().ignoresUnexpectedChildren()) {
            throw new UnexpectedChildException(parent, child, childName);
        }
    }
}
