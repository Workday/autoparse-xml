/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.utils.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * @author nathan.taylor
 * @since 2013-10-3
 */
public class XmlStreamReader {

    private XmlPullParser pullParser;

    XmlStreamReader(XmlPullParser pullParser) {
        this.pullParser = pullParser;
    }

    public Object getProperty(String name)
            throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    public int next()
            throws ParseException {
        try {
            return pullParser.next();
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }


    public void require(int type, String namespaceURI, String localName) {
        throw new UnsupportedOperationException();
    }


    public String getElementText() {
        throw new UnsupportedOperationException();
    }


    public int nextTag()
            throws ParseException {
        try {
            return pullParser.nextTag();
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }


    public boolean hasNext()
            throws ParseException {
        try {
            return pullParser.getEventType() != XmlPullParser.END_DOCUMENT;
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }


    public void close() {
        throw new UnsupportedOperationException();
    }


    public boolean isStartElement()
            throws ParseException {
        try {
            return pullParser.getEventType() == XmlPullParser.START_TAG;
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }


    public boolean isEndElement()
            throws ParseException {
        try {
            return pullParser.getEventType() == XmlPullParser.END_TAG;
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }


    public boolean isCharacters()
            throws ParseException {
        try {
            return XmlPullParser.TEXT == pullParser.getEventType();
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }


    public boolean isWhiteSpace()
            throws ParseException {
        try {
            return pullParser.isWhitespace();
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }


    public String getAttributeValue(String namespaceURI, String localName) {
        return pullParser.getAttributeValue(namespaceURI, localName);
    }


    public int getAttributeCount() {
        return pullParser.getAttributeCount();
    }


    public String getAttributeName(int index) {
        String prefix = getAttributePrefix(index);
        String localName = getAttributeLocalName(index);
        return (StringUtils.isNotEmpty(prefix)) ? prefix + ":" + localName : localName;
    }


    public String getAttributeNamespace(int index) {
        return pullParser.getAttributeNamespace(index);
    }


    public String getAttributeLocalName(int index) {
        return pullParser.getAttributeName(index);
    }


    public String getAttributePrefix(int index) {
        return pullParser.getAttributePrefix(index);
    }


    public String getAttributeType(int index) {
        return pullParser.getAttributeType(index);
    }


    public String getAttributeValue(int index) {
        return pullParser.getAttributeValue(index);
    }


    public boolean isAttributeSpecified(int index) {
        throw new UnsupportedOperationException();
    }

    public int getEventType()
            throws ParseException {
        try {
            return pullParser.getEventType();
        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        }
    }

    public String getText() {
        return pullParser.getText();
    }

    public String getEncoding() {
        return pullParser.getInputEncoding();
    }

    public String getName() {
        String prefix = getPrefix();
        String localName = getLocalName();
        return (StringUtils.isNotEmpty(prefix)) ? prefix + ":" + localName : localName;
    }


    public String getLocalName() {
        return pullParser.getName();
    }


    public boolean hasName()
            throws ParseException {
        return isStartElement() || isEndElement();
    }


    public String getPrefix() {
        return pullParser.getPrefix();
    }

}
