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
 * Indicates to the Autoparse framework that the target field should be inflated from an attribute.
 * This annotation can also be applied to a setter method. The target method must be non-private and
 * must take exactly one parameter.
 *
 * @author nathan.taylor
 * @since 2013-9-19
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XmlAttribute {

    /**
     * The name of the attribute from which the value for this field will be inflated. Multiple
     * names declared here indicate that the attribute could map to more than one name. The
     * attribute will be populated from the first name in the list that is present in the element.
     */
    String[] value();
}
