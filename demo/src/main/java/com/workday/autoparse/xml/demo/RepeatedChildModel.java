/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlTextContent;

/**
 * @author nathan.taylor
 * @since 2013-9-23-15:59
 */
@XmlElement("Repeated_Child_Model")
public class RepeatedChildModel {

    @XmlAttribute("value")
    String value;

    @XmlTextContent
    String textContent;
}
