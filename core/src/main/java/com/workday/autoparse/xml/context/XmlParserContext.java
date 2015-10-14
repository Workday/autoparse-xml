/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.context;

import com.workday.autoparse.xml.parser.CompositeParserMap;
import com.workday.autoparse.xml.parser.GeneratedClassNames;
import com.workday.autoparse.xml.parser.KeyCollisionTester;
import com.workday.autoparse.xml.parser.ParserMap;
import com.workday.autoparse.xml.parser.XmlElementParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A pojo that holds information needed by the tools invoked by the {@link XmlElementParser}s.
 *
 * @author nathan.taylor
 * @since 2013-9-23
 */
public class XmlParserContext {

    private final XmlParserSettings settings;
    private final ParserMap parserMap;

    public XmlParserContext(XmlParserSettings settings) {
        this.settings = settings;
        parserMap = constructParserMap(settings.getPartitionPackages());
    }

    public XmlParserSettings getSettings() {
        return settings;
    }

    public ParserMap getParserMap() {
        return parserMap;
    }

    private static ParserMap constructParserMap(Collection<String> packageNames) {
        if (packageNames.size() == 1) {
            return getParserMapInstance(packageNames.iterator().next());
        }

        final List<ParserMap> components = new ArrayList<>();
        for (String packageName : packageNames) {
            components.add(getParserMapInstance(packageName));
        }

        KeyCollisionTester.validateMaps(components);
        return new CompositeParserMap(components);
    }

    private static ParserMap getParserMapInstance(String packageName) {
        try {
            String parserMapSimpleName = GeneratedClassNames
                    .CLASS_GENERATED_PARSER_MAP;
            String parserMapFullName = GeneratedClassNames.getQualifiedName(packageName,
                                                                            parserMapSimpleName);
            return (ParserMap) Class.forName(parserMapFullName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
