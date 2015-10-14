/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import com.workday.autoparse.xml.context.XmlParserContext;
import com.workday.autoparse.xml.context.XmlParserSettings;
import com.workday.autoparse.xml.context.XmlParserSettingsBuilder;

/**
 * The standard way to create a new {@link XmlStreamParser}.
 *
 * @author nathan.taylor
 * @since 2013-9-23-12:22
 */
public class XmlStreamParserFactory {

    //CHECKSTYLE.OFF: LineLength
    private static final XmlParserSettings DEFAULT_SETTINGS
            = new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                .UnknownElementHandling.IGNORE)
                                            .ignoreUnexpectedChildren(false)
                                            .withPartitions(XmlParserSettingsBuilder
                                                                    .DEFAULT_PACKAGE)
                                            .build();
    //CHECKSTYLE.ON: LineLength

    private XmlStreamParserFactory() {
    }

    public static XmlStreamParser newXmlStreamParser(XmlParserSettings settings) {
        XmlParserContext context = new XmlParserContext(settings);

        return new StandardXmlStreamParser(context);
    }

    public static XmlStreamParser newXmlStreamParser() {
        return newXmlStreamParser(DEFAULT_SETTINGS);
    }
}
