/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

/**
 * @author nathan.taylor
 * @since 2013-10-10
 */


import com.google.common.collect.Lists;
import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;

import java.util.List;

@XmlElement("Alternate_Attribute_Model")
public class AlternateAttributeModel {

    @XmlAttribute({"int1", "int2", "int3"})
    int anInt;

    @XmlChildElement
    List<AlternateAttributeModel> children = Lists.newArrayList();
}
