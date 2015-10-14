/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo.partition2;

import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author nathan.taylor
 * @since 2015-03-02
 */
@XmlElement("Partitioned_Model2")
public class PartitionedModel2 {

    @XmlAttribute("string")
    public String string;

    public PartitionedModel2() {
    }

    public PartitionedModel2(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
