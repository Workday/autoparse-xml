/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.utils;

/**
 * @author nathan.taylor
 * @since 2015-10-23.
 */
public class Preconditions {

    private Preconditions() {
    }

    public static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotNull(Object reference, String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
    }

    public static void checkState(boolean condidtion, String message) {
        if (!condidtion) {
            throw new IllegalStateException(message);
        }
    }
}
