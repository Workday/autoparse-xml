/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlPostParse;

/**
 * @author nathan.taylor
 * @since 2013-10-16
 */
@XmlElement("Post_Parse_Model")
public class PostParseModel {

    boolean postParseCalled;

    @XmlPostParse
    void postParse() {
        postParseCalled = true;
    }
}
