/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.context.XmlContextHolder;
import com.workday.autoparse.xml.utils.StringTransformer;
import com.workday.autoparse.xml.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nathan.taylor
 * @since 2013-9-19
 */
public class Attributes {

    private final Map<String, String> attributeMap;
    private final String contentString;
    private final List<StringTransformer> stringTransformers;

    public Attributes(XmlStreamReader reader)
            throws ParseException {
        stringTransformers = XmlContextHolder.getContext().getSettings().getStringTransformers();

        attributeMap = new HashMap<>();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            attributeMap.put(reader.getAttributeName(i), reader.getAttributeValue(i));
        }
        String string = null;
        reader.next();
        if (reader.isCharacters()) {
            string = reader.getText();
            reader.next();
        }
        contentString = string != null ? applyFilters(string) : null;
    }

    public String getStringAttributeWithName(String name) {
        String attribute = attributeMap.get(name);
        if (attribute != null) {
            return applyFilters(attribute);
        }
        return null;
    }

    public boolean getBooleanAttributeWithName(String name) {
        String string = attributeMap.get(name);
        if (StringUtils.isNotEmpty(string)) {
            string = string.trim();
            return "1".equals(string);
        }
        return false;
    }

    public int getIntAttributeWithName(String name)
            throws NumberFormatException {
        int value = -1;
        String stringValue = attributeMap.get(name);
        if (StringUtils.isNotEmpty(stringValue)) {
            value = Integer.parseInt(stringValue.trim());
        }
        return value;
    }

    public byte getByteAttributeWithName(String name) {
        byte value = 0;
        String stringValue = attributeMap.get(name);
        if (StringUtils.isNotEmpty(stringValue)) {
            value = Byte.parseByte(stringValue);
        }
        return value;
    }

    public short getShortAttributeWithName(String name) {
        short value = 0;
        String stringValue = attributeMap.get(name);
        if (StringUtils.isNotEmpty(stringValue)) {
            value = Short.parseShort(stringValue);
        }
        return value;
    }

    public long getLongAttributeWithName(String name) {
        long value = 0;
        String stringValue = attributeMap.get(name);
        if (StringUtils.isNotEmpty(stringValue)) {
            value = Long.parseLong(stringValue);
        }
        return value;
    }

    public double getDoubleAttributeWithName(String name) {
        double value = 0;
        String stringValue = attributeMap.get(name);
        if (StringUtils.isNotEmpty(stringValue)) {
            value = Double.parseDouble(stringValue);
        }
        return value;
    }

    public float getFloatAttributeWithName(String name) {
        float value = 0;
        String stringValue = attributeMap.get(name);
        if (StringUtils.isNotEmpty(stringValue)) {
            value = Float.parseFloat(stringValue);
        }
        return value;
    }

    public char getCharAttributeWithName(String name) {
        char value = 0;
        String stringValue = attributeMap.get(name);
        if (stringValue != null && stringValue.length() == 1) {
            value = stringValue.charAt(0);
        }
        return value;
    }

    public String getContentString() {
        return contentString;
    }

    public boolean hasAttribute(String name) {
        return attributeMap.containsKey(name);
    }

    private String applyFilters(String original) {
        String result = original;
        if (!stringTransformers.isEmpty()) {
            for (StringTransformer transformer : stringTransformers) {
                result = transformer.transform(result);
            }
        }
        return result;
    }
}
