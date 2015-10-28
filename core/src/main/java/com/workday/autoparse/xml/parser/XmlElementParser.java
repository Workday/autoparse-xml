/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.annotations.XmlElement;

/**
 * A autoparse corresponding to a single class annotated with {@link XmlElement}. All
 * implementations will be generated. These parsers will be instantiated and called by the
 * framework. Clients will not need to perform any operations on these objects.
 * <p/>
 * Implementations will be required to have a {@code public static INSTANCE} field.
 *
 * @param <T> The type of the object that will be created. This is type annotated with ParseFor.
 *
 * @author nathan.taylor
 * @since 2013-9-17
 */
public interface XmlElementParser<T> {

    /**
     * Instantiates and populates an object of type {@code T}. When complete, the reader must be at
     * the event immediately following the current element's {@link javax.xml.stream
     * .XMLStreamConstants#END_ELEMENT} event.
     *
     * @param reader The XMLStreamReader on which to operate.
     *
     * @return The fully inflated object.
     */
    T parseElement(XmlStreamReader reader)
            throws UnknownElementException, UnexpectedChildException, ParseException;
}
