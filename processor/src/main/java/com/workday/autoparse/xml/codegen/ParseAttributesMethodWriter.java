/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.google.common.collect.Lists;
import com.squareup.javawriter.JavaWriter;
import com.workday.autoparse.xml.parser.ParseException;
import com.workday.autoparse.xml.parser.XmlElementParser;
import com.workday.autoparse.xml.parser.XmlStreamReader;
import com.workday.meta.MetaTypes;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Writes the {@code parseAttributes} method in an {@link XmlElementParser}.
 *
 * @author nathan.taylor
 * @since 2013-10-14
 */
class ParseAttributesMethodWriter {

    private final AttributesAndElements attributesAndElements;
    private final ProcessingEnvironment processingEnv;
    private final MetaTypes metaTypes;

    public ParseAttributesMethodWriter(AttributesAndElements attributesAndElements,
                                       ProcessingEnvironment processingEnv,
                                       MetaTypes metaTypes) {
        this.attributesAndElements = attributesAndElements;
        this.processingEnv = processingEnv;
        this.metaTypes = metaTypes;
    }

    public void writeParseAttributesMethod(TypeElement classElement,
                                           JavaWriter writer)

            throws IOException {
        List<String> parameters = new ArrayList<String>(4);
        parameters.add(classElement.getSimpleName().toString());
        parameters.add("object");
        parameters.add(XmlStreamReader.class.getSimpleName());
        parameters.add("reader");

        writer.beginMethod("void", "parseAttributes", EnumSet.of(Modifier.PRIVATE), parameters,
                           Lists.newArrayList(ParseException.class.getSimpleName()));
        writer.emitStatement("Attributes attributes = new Attributes(reader)");

        for (Map.Entry<List<String>, Element> entry : attributesAndElements.getAttributes()
                                                                           .entrySet()) {
            List<String> names = entry.getKey();
            Element attributeElement = entry.getValue();

            writeAttributeAssignment(names, attributeElement, writer);
        }

        Element textContent = attributesAndElements.getTextContentElement();
        writer.emitField("String",
                         "contentString",
                         EnumSet.noneOf(Modifier.class),
                         "attributes.getContentString()");
        writer.beginControlFlow("if (contentString != null)");
        if (textContent instanceof ExecutableElement) {
            writer.emitStatement("object.%s(contentString)", textContent.getSimpleName());
        } else if (textContent instanceof VariableElement) {
            writer.emitStatement("object.%s = contentString", textContent.getSimpleName());
        }
        writer.endControlFlow();
        writer.endMethod();
    }

    private void writeAttributeAssignment(List<String> names,
                                          Element attributeElement,
                                          JavaWriter writer)
            throws IOException {

        String ifPattern = "if (attributes.hasAttribute(\"%s\"))";
        String initializationPattern = getAttributeInitializationPattern(attributeElement);
        String assignmentPattern;
        if (attributeElement instanceof ExecutableElement) {
            assignmentPattern = String.format(Locale.US,
                                              "object.%s(%s)",
                                              attributeElement.getSimpleName(),
                                              initializationPattern);
        } else {
            assignmentPattern = String.format(Locale.US,
                                              "object.%s = %s",
                                              attributeElement.getSimpleName(),
                                              initializationPattern);
        }

        String name = names.get(0);
        writer.beginControlFlow(String.format(ifPattern, name));
        writer.emitStatement(assignmentPattern, name);

        for (int i = 1; i < names.size(); i++) {
            name = names.get(i);
            writer.nextControlFlow(String.format("else " + ifPattern, name));
            writer.emitStatement(assignmentPattern, name);
        }

        writer.endControlFlow();
    }

    private String getAttributeInitializationPattern(Element element) {
        String method = null;
        TypeMirror type;
        if (element instanceof ExecutableElement) {
            type = ((ExecutableElement) element).getParameters().get(0).asType();
        } else {
            type = element.asType();
        }
        String stringMethod = "attributes.getStringAttributeWithName(\"%s\")";

        if (metaTypes.isBoxable(type)) {
            String primitiveName = StringUtils.capitalize(metaTypes.asPrimitive(type).toString());
            method = "attributes.get" + primitiveName + "AttributeWithName(\"%s\")";
        } else if (metaTypes.isString(type)) {
            method = stringMethod;
        } else if (metaTypes.isSameType(type, BigDecimal.class)
                || metaTypes.isSameType(type, BigInteger.class)) {
            method = "new " + type.toString() + "(" + stringMethod + ")";
        } else {
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR,
                                       String.format("%s is not a supported attribute type", type),
                                       element);
        }
        return method;
    }
}
