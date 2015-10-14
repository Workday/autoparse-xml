/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.parser;

/**
 * @author nathan.taylor
 * @since 2013-9-18
 */
public class UnknownElementException
        extends Exception {


    private static final long serialVersionUID = 1986382993464212104L;

    public UnknownElementException(String name) {
        super(name);
    }

}
