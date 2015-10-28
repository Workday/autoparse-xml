/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.squareup.javawriter.JavaWriter;
import com.workday.autoparse.xml.parser.ParseException;
import com.workday.autoparse.xml.parser.UnexpectedChildException;
import com.workday.autoparse.xml.parser.UnexpectedElementHandler;
import com.workday.autoparse.xml.parser.UnknownElementException;
import com.workday.autoparse.xml.parser.XmlElementParser;
import com.workday.autoparse.xml.parser.XmlStreamReader;
import com.workday.autoparse.xml.utils.CollectionUtils;
import com.workday.meta.Initializers;
import com.workday.meta.InvalidTypeException;
import com.workday.meta.MetaTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Writes the {@code parseChildren} method in an {@link XmlElementParser}.
 *
 * @author nathan.taylor
 * @since 2013-10-14
 */
class ParseChildrenMethodWriter {

    private final AttributesAndElements attributesAndElements;
    private final ProcessingEnvironment processingEnv;
    private final MetaTypes metaTypes;
    private final TypeElement classElement;
    private final Initializers initializers;

    public ParseChildrenMethodWriter(AttributesAndElements attributesAndElements,
                                     ProcessingEnvironment processingEnv,
                                     MetaTypes metaTypes,
                                     TypeElement classElement) {
        this.attributesAndElements = attributesAndElements;
        this.processingEnv = processingEnv;
        this.metaTypes = metaTypes;
        this.classElement = classElement;
        initializers = new Initializers(metaTypes);
    }

    public void writeParseChildrenMethod(TypeElement classElement, JavaWriter writer)
            throws IOException {

        List<String> parameters = new ArrayList<String>(4);
        parameters.add(classElement.getSimpleName().toString());
        parameters.add("object");
        parameters.add(XmlStreamReader.class.getSimpleName());
        parameters.add("reader");

        List<String> throwsTypes = CollectionUtils.newArrayList(
                ParseException.class.getSimpleName(),
                UnknownElementException.class.getSimpleName(),
                UnexpectedChildException.class.getSimpleName());

        writer.beginMethod("void",
                           "parseChildren",
                           EnumSet.of(Modifier.PRIVATE),
                           parameters,
                           throwsTypes);

        initializeSetters(writer);

        writer.beginControlFlow("while (!reader.isEndElement())");
        writer.emitStatement(
                "Preconditions.checkState(reader.isStartElement(), \"Expected to be at a start "
                        + "element\")");
        writer.emitStatement("String name = reader.getName()");
        writer.emitStatement("Object child = ParserUtils.parseCurrentElement(reader)");

        boolean first = true;
        first = writeChildIfStatements(writer, first);

        String unexpectedChildStatement =
                String.format("%s.handleUnexpectedChild(object, child, name)",
                              UnexpectedElementHandler.class.getSimpleName());
        if (first) {
            writer.emitStatement(unexpectedChildStatement);
        } else {
            writer.nextControlFlow("else if (child == null)");
            writer.emitSingleLineComment("do nothing");
            writer.nextControlFlow("else");
            writer.emitStatement(unexpectedChildStatement);
            writer.endControlFlow();
        }

        writer.endControlFlow();

        writeSetterAssignments(writer);
        writer.endMethod();
    }

    private void initializeSetters(JavaWriter writer)
            throws IOException {
        for (ExecutableElement singletonSetter
                : attributesAndElements.getSingletonSetterChildren()) {
            String methodName = singletonSetter.getSimpleName().toString();
            DeclaredType parameterType =
                    (DeclaredType) singletonSetter.getParameters().get(0).asType();
            String parameterTypeName = writer.compressType(parameterType.toString());
            writer.emitStatement("%s %sValue = %s",
                                 parameterTypeName,
                                 methodName,
                                 findSingletonInitialValue(parameterType));
        }
        for (ExecutableElement collectionSetter : attributesAndElements
                .getCollectionSetterChildren()) {
            String methodName = collectionSetter.getSimpleName().toString();
            VariableElement parameter = collectionSetter.getParameters().get(0);
            DeclaredType parameterType = (DeclaredType) parameter.asType();
            String initializer = null;
            try {
                initializer = initializers.findCollectionInitializer(parameterType);
            } catch (InvalidTypeException e) {
                processingEnv.getMessager()
                             .printMessage(Diagnostic.Kind.ERROR, e.getMessage(), collectionSetter);
            }
            writer.emitField(parameterType.toString(),
                             methodName + "Value",
                             EnumSet.noneOf(Modifier.class),
                             initializer);
        }
    }

    private void writeSetterAssignments(JavaWriter writer)
            throws IOException {
        for (ExecutableElement singletonSetter
                : attributesAndElements.getSingletonSetterChildren()) {
            writer.emitStatement("object.%s(%sValue)", singletonSetter.getSimpleName(),
                                 singletonSetter.getSimpleName());
        }
        for (ExecutableElement collectionSetter
                : attributesAndElements.getCollectionSetterChildren()) {
            writer.emitStatement("object.%s(%sValue)", collectionSetter.getSimpleName(),
                                 collectionSetter.getSimpleName());
        }
    }

    private String findSingletonInitialValue(DeclaredType type) {
        String initialValue;
        if (metaTypes.isBoxable(type)) {
            TypeKind kind = metaTypes.asPrimitive(type).getKind();

            if (kind == TypeKind.BOOLEAN) {
                initialValue = "false";
            } else if (kind == TypeKind.CHAR) {
                initialValue = "'\0'";
            } else {
                initialValue = "0";
            }
        } else {
            initialValue = "null";
        }
        return initialValue;
    }

    private String findCollectionInitializer(DeclaredType type)
            throws IllegalArgumentException {
        String initializer;

        if (metaTypes.isSameTypeErasure(type, List.class)
                || metaTypes.isSameTypeErasure(type, ArrayList.class)) {
            initializer = "Lists.newArrayList()";
        } else if (metaTypes.isSameTypeErasure(type, LinkedList.class)) {
            initializer = "Lists.newLinkedList()";
        } else if (metaTypes.isSameTypeErasure(type, Set.class)
                || metaTypes.isSameTypeErasure(type, HashSet.class)) {
            initializer = "Sets.newHashSet()";
        } else if (metaTypes.isSameTypeErasure(type, LinkedHashSet.class)) {
            initializer = "Sets.newLinkedHashSet()";
        } else if (metaTypes.isSameTypeErasure(type, TreeSet.class)) {
            initializer = "Sets.newTreeSet()";
        } else {
            throw new IllegalArgumentException(
                    String.format("Autoparse does not know how to instantiate Collection of type "
                                          + "%s",
                                  type.toString()));
        }
        return initializer;
    }

    private boolean writeChildIfStatements(JavaWriter writer, boolean first)
            throws IOException {
        // TODO: assign children according to the highest specificity metric
        for (Element e : attributesAndElements.getSingletonFieldChildren()) {
            first = writeSingletonChildIfStatement(e, writer, first);
        }
        for (Element e : attributesAndElements.getCollectionFieldChildren()) {
            first = writeCollectionChildIfStatement(e, writer, first);
        }
        for (Element e : attributesAndElements.getSingletonSetterChildren()) {
            first = writeSingletonChildIfStatement(e, writer, first);
        }
        for (Element e : attributesAndElements.getCollectionSetterChildren()) {
            first = writeCollectionChildIfStatement(e, writer, first);
        }
        return first;
    }

    private boolean writeSingletonChildIfStatement(Element element,
                                                   JavaWriter writer,
                                                   boolean first)
            throws IOException {
        String typeName = extractChildTypeName(element, writer, false);
        String assignmentStatement;
        String singletonValidationStatement;
        if (element instanceof ExecutableElement) {
            assignmentStatement =
                    String.format("%sValue = (%s) child", element.getSimpleName(), typeName);
            singletonValidationStatement =
                    String.format("%sValue == null", element.getSimpleName());
        } else {
            assignmentStatement =
                    String.format("object.%s = (%s) child", element.getSimpleName(), typeName);
            singletonValidationStatement =
                    String.format("object.%s == null", element.getSimpleName());
        }
        String ifStatement;

        ifStatement = String.format("if (child instanceof %s)", typeName);

        if (first) {
            first = false;
            writer.beginControlFlow(ifStatement);
        } else {
            writer.nextControlFlow("else " + ifStatement);
        }

        String errorMessage =
                String.format(
                        "Expected only one child of type %s for parent type %s, but found multiple",
                        typeName,
                        classElement.getSimpleName());
        writer.emitStatement("Preconditions.checkState(%s, \"%s\")",
                             singletonValidationStatement,
                             errorMessage);
        writer.emitStatement(assignmentStatement);
        return first;
    }

    private boolean writeCollectionChildIfStatement(Element element,
                                                    JavaWriter writer,
                                                    boolean first)
            throws IOException {

        String parameterTypeName = extractChildTypeName(element, writer, true);
        String addStatement;

        if (element instanceof ExecutableElement) {
            addStatement = String.format("%sValue.add((%s) child)",
                                         element.getSimpleName(),
                                         parameterTypeName);
        } else {
            addStatement = String.format("object.%s.add((%s) child)",
                                         element.getSimpleName(),
                                         parameterTypeName);
        }
        String ifStatement = String.format("if (child instanceof %s)", parameterTypeName);

        if (first) {
            first = false;
            writer.beginControlFlow(ifStatement);
        } else {
            writer.nextControlFlow("else " + ifStatement);
        }

        writer.emitStatement(addStatement);
        return first;
    }

    /**
     * @param element The method or field that takes either a parsed object or a collection of
     * parsed objects.
     *
     * @return The compressed name of the parsed type.
     */
    private String extractChildTypeName(Element element, JavaWriter writer, boolean isCollection) {
        String typeName;
        if (element instanceof ExecutableElement) {
            DeclaredType declaredType =
                    (DeclaredType) ((ExecutableElement) element).getParameters().get(0).asType();
            if (isCollection) {
                declaredType = getCollectionParameterType(declaredType, element);
            }
            typeName = writer.compressType(declaredType.toString());
        } else {
            DeclaredType declaredType = (DeclaredType) element.asType();
            if (isCollection) {
                declaredType = getCollectionParameterType(declaredType, element);
            }
            typeName = writer.compressType(declaredType.toString());
        }
        return typeName;
    }

    private DeclaredType getCollectionParameterType(DeclaredType collectionType,
                                                    Element offendingElement) {
        DeclaredType declaredParameterType;
        TypeMirror parameterType = collectionType.getTypeArguments().get(0);
        if (!(parameterType instanceof DeclaredType)) {
            String errorMessage =
                    "Autoparse cannot handle collections parametrised with non-declared types.";
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR, errorMessage, offendingElement);
            Elements elementUtils = processingEnv.getElementUtils();
            TypeElement typeElement = elementUtils.getTypeElement(Object.class.getCanonicalName());
            declaredParameterType = (DeclaredType) typeElement.asType();
        } else {
            declaredParameterType = (DeclaredType) parameterType;
        }
        return declaredParameterType;
    }

}
