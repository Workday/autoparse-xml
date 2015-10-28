/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.context.XmlContextHolder;
import com.workday.autoparse.xml.context.XmlParserContext;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * The entry point for parsing an XML input stream into objects annotated with {@link XmlElement}.
 *
 * @author nathan.taylor
 * @since 2013-9-18-11:41
 */
class StandardXmlStreamParser implements XmlStreamParser {

    private final XmlParserContext context;

    StandardXmlStreamParser(XmlParserContext context) {
        this.context = context;
    }

    @Override
    public Object parseStream(InputStream in) throws ParseException,
            UnknownElementException,
            UnexpectedChildException {

        Object result = null;
        XmlStreamReader reader = null;

        try {
            XmlContextHolder.setContext(context);
            reader = XmlStreamReaderFactory.newXmlStreamReader(in);

            while (reader.hasNext()) {
                reader.next();
                if (reader.isStartElement()) {
                    result = ParserUtils.parseCurrentElement(reader);
                    break;
                }
            }
        } finally {
            XmlContextHolder.removeContext();
            if (in != null) {
                closeQuietly(in);
            }
        }
        return result;
    }

    private void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
