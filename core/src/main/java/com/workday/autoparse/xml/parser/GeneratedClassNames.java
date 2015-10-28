/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import java.util.Locale;

/**
 * @author nathan.taylor
 * @since 2014-10-17.
 */
public final class GeneratedClassNames {

    public static final String CLASS_GENERATED_PARSER_MAP = "__AutoparseGeneratedParserMap";
    public static final String PARSER_SUFFIX = "$$" + XmlElementParser.class.getSimpleName();

    private GeneratedClassNames() {
    }

    public static String getQualifiedName(String packageName, String className) {
        return String.format(Locale.US, "%s.%s", packageName, className);
    }
}
