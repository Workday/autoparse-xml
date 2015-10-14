/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlElement;

/**
 * @author nathan.taylor
 * @since 2013-9-20-17:28
 */
@XmlElement("Child_Model")
public class ChildModel {

    @XmlAttribute("My_String")
    String myString;

    @XmlAttribute("wcl:My_Int")
    int myInt;
}
