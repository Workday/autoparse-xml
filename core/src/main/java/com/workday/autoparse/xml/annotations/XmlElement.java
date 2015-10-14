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
 * Indicate to the AutoParse framework that a new instance of the target class should be created and
 * inflated from XML elements with the corresponding names.
 *
 * @author nathan.taylor
 * @since 2013-09-30
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface XmlElement {

    /**
     * The set of XMl element names, each of which will signal the creation of a new instance of the
     * target class.
     */
    String[] value();

}
