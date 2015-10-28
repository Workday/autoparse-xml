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
 * Indicate to the Autoparse framework that a new instance of the target class should be
 * instantiated and inflated when unknown XML element tags are encountered. An unknown XML element
 * tag is one that was not declared in any {@link XmlElement} annotations. If the target class
 * contains other parsing annotations (e.g. @{@link XmlChildElement}), they will be honored.
 * <p/>
 * <b>NOTE:</b> Only one class may be given this annotation. Assigning this annotation to multiple
 * classes will result in a compile time error.
 *
 * @author nathan.taylor
 * @since 2014-7-8
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface XmlUnknownElement {

}
