/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import java.util.Set;

/**
 * A map from XML element name to {@link XmlElementParser}.
 *
 * @author nathan.taylor
 * @since 2013-9-19
 */
public interface ParserMap {

    XmlElementParser<?> get(String name);

    Set<String> keySet();

}
