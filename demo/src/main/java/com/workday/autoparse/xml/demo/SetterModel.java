/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlTextContent;

import java.util.Set;

/**
 * @author nathan.taylor
 * @since 2013-10-10
 */
@XmlElement("Setter_Model")
public class SetterModel {

    int anInt;

    ChildModel child;

    Set<RepeatedChildModel> repeatedChildren;

    String textContent;

    @XmlAttribute("int")
    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    @XmlChildElement
    public void setChild(ChildModel child) {
        this.child = child;
    }

    @XmlChildElement
    public void setRepeatedChildren(Set<RepeatedChildModel> repeatedChildren) {
        this.repeatedChildren = repeatedChildren;
    }

    @XmlTextContent
    void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
