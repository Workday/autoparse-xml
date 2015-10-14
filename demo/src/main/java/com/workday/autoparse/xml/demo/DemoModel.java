/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.google.common.collect.Lists;
import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlTextContent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author nathan.taylor
 * @since 2013-9-17- 15:04
 */
@XmlElement("wcl:Demo_Model")
public class DemoModel {

    @XmlAttribute("My_Big_Decimal")
    BigDecimal myBigDecimal = BigDecimal.ONE;

    @XmlAttribute("My_Big_Integer")
    BigInteger myBigInteger = BigInteger.TEN;

    @XmlAttribute("wcl:My_Primitive_Boolean")
    boolean myPrimitiveBoolean = true;

    @XmlAttribute("My_Boxed_Boolean")
    Boolean myBoxedBoolean = true;

    @XmlAttribute("My_Primitive_Byte")
    byte myPrimitiveByte = -1;

    @XmlAttribute("My_Boxed_Byte")
    Byte myBoxedByte = -1;

    @XmlAttribute("My_Primitive_Char")
    char myPrimitiveChar = 'a';

    @XmlAttribute("My_Boxed_Char")
    Character myBoxedChar = 'a';

    @XmlAttribute("My_Primitive_Double")
    double myPrimitiveDouble = -1.0;

    @XmlAttribute("My_Boxed_Double")
    Double myBoxedDouble = -1.0;

    @XmlAttribute("My_Primitive_Float")
    float myPrimitiveFloat = -1.0f;

    @XmlAttribute("My_Boxed_Float")
    Float myBoxedFloat = -1.0f;

    @XmlAttribute("My_Primitive_Int")
    int myPrimitiveInt = -1;

    @XmlAttribute("My_Boxed_Int")
    Integer myBoxedInt = -1;

    @XmlAttribute("My_Primitive_Long")
    long myPrimitiveLong = -1;

    @XmlAttribute("My_Boxed_Long")
    Long myBoxedLong = -1L;

    @XmlAttribute("My_Primitive_Short")
    short myPrimitiveShort = -1;

    @XmlAttribute("My_Boxed_Short")
    Short myBoxedShort = -1;

    @XmlAttribute("My_String")
    String myString = "default";

    @XmlTextContent
    String myTextContent = "default";

    @XmlChildElement
    ChildModel myChildModel;

    @XmlChildElement
    List<RepeatedChildModel> repeatedChildModels = Lists.newArrayList();

}
