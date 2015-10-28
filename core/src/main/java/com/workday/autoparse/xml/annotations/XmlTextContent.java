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
 * Indicates to the Autoparse framework that the target field should be inflated from the text
 * context of the element. This annotation only makes sense when applied to a {@link String}. This
 * annotation can also be applied to a setter method. The target method must be non-private and must
 * take exactly one parameter, which is of type String.
 *
 * @author nathan.taylor
 * @since 2013-9-26-11:10
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XmlTextContent {
}
