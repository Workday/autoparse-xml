/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.google.common.collect.Lists;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;

import java.util.List;

/**
 * @author nathan.taylor
 * @since 2013-10-10
 */
@XmlElement("Root_Model")
public class RootModel {

    @XmlChildElement
    List<Object> children = Lists.newArrayList();
}
