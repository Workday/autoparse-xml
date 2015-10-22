/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.utils;

/**
 * @author nathan.taylor
 * @since 2015-10-22.
 */
public class StringUtils {

    private StringUtils() {
    }

    public static boolean isNotEmpty(CharSequence charSequence) {
        return charSequence != null && charSequence.length() > 0;
    }

    public static String capitalize(String string) {
        return isNotEmpty(string)
               ? string.substring(0, 1).toUpperCase() + string.substring(1)
               : string;
    }
}
