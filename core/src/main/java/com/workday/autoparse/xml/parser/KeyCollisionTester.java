/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nathan.taylor
 * @since 2015-03-02
 */
public class KeyCollisionTester {

    private KeyCollisionTester() {
    }

    public static void validateMaps(Collection<ParserMap> maps) {
        Map<String, Collection<ParserMap>> validationMap = new HashMap<>();
        for (ParserMap parserMap : maps) {
            for (String key : parserMap.keySet()) {
                putInMap(validationMap, key, parserMap);
            }
        }

        Collection<String> errorMessages = new ArrayList<>();
        for (Map.Entry<String, Collection<ParserMap>> entry : validationMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                errorMessages.add(getErrorMessage(entry.getKey(), entry.getValue()));
            }
        }

        if (!errorMessages.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    "Multiple models map to the same key. The following lists all violations:\n");
            for (String errorMessage : errorMessages) {
                sb.append(errorMessage).append("\n");
            }

            throw new IllegalArgumentException(sb.toString());
        }
    }

    private static void putInMap(Map<String, Collection<ParserMap>> map,
                                 String key,
                                 ParserMap value) {
        Collection<ParserMap> collection = map.get(key);
        if (collection == null) {
            collection = new ArrayList<>();
            map.put(key, collection);
        }
        collection.add(value);
    }

    private static String getErrorMessage(String key, Collection<ParserMap> parserMaps) {
        StringBuilder sb = new StringBuilder("'").append(key).append("' =>\n");
        for (ParserMap parserMap : parserMaps) {
            String parserName = parserMap.get(key).getClass().getCanonicalName();
            String modelName =
                    parserName.endsWith(GeneratedClassNames.PARSER_SUFFIX)
                    ? parserName.substring(0,
                                           parserName.length()
                                                   - GeneratedClassNames.PARSER_SUFFIX.length())
                    : parserName;
            sb.append("   ").append(modelName).append('\n');
        }
        return sb.toString();
    }
}
