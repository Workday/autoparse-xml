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
 * Indicates to the Autoparse framework that the target field should be inflated from a child
 * element. The corresponding class (or a subclass) must be annotated with {@link XmlElement}. If
 * the target of this annotation is a {@link java.util.Collection}, then the inflated child object
 * will be added to it.
 * <p/>
 * This annotation can also be used on a setter method. The method must be non-private and must take
 * exactly one parameter. The rules for fields apply to the parameter.
 * <p/>
 * <b>NOTE:</b> If the target of this annotation is a Collection field, then that list must be
 * instantiated by the class. The framework will NOT instantiate any fields that take the type
 * Collection.
 *
 * @author nathan.taylor
 * @since 2013-9-19
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XmlChildElement {

}
