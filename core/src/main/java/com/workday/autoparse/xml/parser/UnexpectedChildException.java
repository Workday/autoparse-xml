/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

/**
 * @author nathan.taylor
 * @since 2013-9-23-11:59
 */
public class UnexpectedChildException extends Exception {

    private static final long serialVersionUID = 693641020967838317L;

    public UnexpectedChildException(Object parent, Object child, String childName) {
        super(String.format("Parsed unexpected child of type %s for name %s in parent of type %s.",
                            child.getClass().getCanonicalName(),
                            childName,
                            parent.getClass().getCanonicalName()));
    }
}
