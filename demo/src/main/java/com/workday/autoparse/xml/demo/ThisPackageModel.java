/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.demo.other.OtherPackageModel;

/**
 * @author nathan.taylor
 * @since 2013-10-14
 */
@XmlElement("This_Package_Model")
public class ThisPackageModel {

    @XmlChildElement
    OtherPackageModel otherPackageModel;
}
