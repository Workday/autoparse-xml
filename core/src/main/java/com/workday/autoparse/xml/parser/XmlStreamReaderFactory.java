/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author nathan.taylor
 * @since 2013-10-3
 */
class XmlStreamReaderFactory {

    private XmlStreamReaderFactory() {
    }

    public static XmlStreamReader newXmlStreamReader(InputStream in)
            throws ParseException {
        try {
            XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            pullParser.setInput(inputStreamReader);
            return new XmlStreamReader(pullParser);
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }
}
