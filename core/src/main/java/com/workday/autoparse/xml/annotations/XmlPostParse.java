/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method with this annotation will be called immediately after the object has been fully
 * instantiated by the Autoparse framework. The method must take no parameters and must be
 * non-private.
 *
 * @author nathan.taylor
 * @since 2013-10-16
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface XmlPostParse {
}
