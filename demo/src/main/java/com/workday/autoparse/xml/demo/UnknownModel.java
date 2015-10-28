/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlUnknownElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nathan.taylor
 * @since 2014-7-8
 */
@XmlUnknownElement
public class UnknownModel {

    @XmlChildElement
    List<Object> children = new ArrayList<>();
}
