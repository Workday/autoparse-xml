/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.squareup.javawriter.JavaWriter;
import com.workday.autoparse.xml.annotations.XmlPostParse;
import com.workday.autoparse.xml.parser.Attributes;
import com.workday.autoparse.xml.parser.GeneratedClassNames;
import com.workday.autoparse.xml.parser.ParseException;
import com.workday.autoparse.xml.parser.ParserUtils;
import com.workday.autoparse.xml.parser.UnexpectedChildException;
import com.workday.autoparse.xml.parser.UnexpectedElementHandler;
import com.workday.autoparse.xml.parser.UnknownElementException;
import com.workday.autoparse.xml.parser.XmlElementParser;
import com.workday.autoparse.xml.parser.XmlStreamReader;
import com.workday.autoparse.xml.utils.CollectionUtils;
import com.workday.autoparse.xml.utils.Preconditions;
import com.workday.meta.MetaTypes;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Generates implementations of {@link XmlElementParser}.
 *
 * @author nathan.taylor
 * @since 2013-9-17- 16:56
 */
class XmlElementParserGenerator {

    private final ProcessingEnvironment processingEnv;
    private final TypeElement classElement;
    private final ParseAttributesMethodWriter parseAttributesMethodWriter;
    private final ParseChildrenMethodWriter parseChildrenMethodWriter;

    XmlElementParserGenerator(ProcessingEnvironment processingEnv, TypeElement classElement) {

        this.processingEnv = processingEnv;
        this.classElement = classElement;
        MetaTypes metaTypes = new MetaTypes(processingEnv);
        AttributesAndElements attributesAndElements =
                new AttributesAndElements(processingEnv, classElement);
        parseAttributesMethodWriter =
                new ParseAttributesMethodWriter(attributesAndElements, processingEnv, metaTypes);
        parseChildrenMethodWriter =
                new ParseChildrenMethodWriter(attributesAndElements,
                                              processingEnv,
                                              metaTypes,
                                              classElement);

    }

    public void generateParser()
            throws IOException {

        String parserName =
                classElement.getQualifiedName().toString() + GeneratedClassNames.PARSER_SUFFIX;
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(parserName);

        JavaWriter writer = new JavaWriter(sourceFile.openWriter());
        writer.emitPackage(processingEnv.getElementUtils()
                                        .getPackageOf(classElement)
                                        .getQualifiedName()
                                        .toString());
        writer.emitImports(getStandardImports());
        writer.emitEmptyLine();

        String xmlElementParserName =
                JavaWriter.type(XmlElementParser.class, classElement.getSimpleName().toString());
        writer.beginType(parserName,
                         "class",
                         EnumSet.of(Modifier.PUBLIC, Modifier.FINAL),
                         null,
                         xmlElementParserName);
        writer.emitEmptyLine();

        writer.emitField(parserName,
                         "INSTANCE",
                         EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
                         String.format("new %s()", writer.compressType(parserName)));
        writer.emitEmptyLine();

        // Constructor
        writer.beginMethod(null, parserName, EnumSet.of(Modifier.PRIVATE));
        writer.endMethod();
        writer.emitEmptyLine();

        writeParseElementMethod(writer);
        writer.emitEmptyLine();
        parseAttributesMethodWriter.writeParseAttributesMethod(classElement, writer);
        writer.emitEmptyLine();
        parseChildrenMethodWriter.writeParseChildrenMethod(classElement, writer);
        writer.emitEmptyLine();

        writer.endType();
        writer.close();
    }

    private Set<String> getStandardImports() {
        Set<String> results = new HashSet<>();
        results.add(List.class.getCanonicalName());
        results.add(Attributes.class.getCanonicalName());
        results.add(Preconditions.class.getCanonicalName());
        results.add(CollectionUtils.class.getCanonicalName());
        results.add(ParseException.class.getCanonicalName());
        results.add(ParserUtils.class.getCanonicalName());
        results.add(Set.class.getCanonicalName());
        results.add(UnexpectedChildException.class.getCanonicalName());
        results.add(UnexpectedElementHandler.class.getCanonicalName());
        results.add(UnknownElementException.class.getCanonicalName());
        results.add(XmlElementParser.class.getCanonicalName());
        results.add(XmlStreamReader.class.getCanonicalName());
        return results;

    }

    private void writeParseElementMethod(JavaWriter writer)
            throws IOException {

        List<String> parameters =
                CollectionUtils.newArrayList(XmlStreamReader.class.getSimpleName(), "reader");

        List<String> throwsTypes = CollectionUtils.newArrayList(
                ParseException.class.getSimpleName(),
                UnknownElementException.class.getSimpleName(),
                UnexpectedChildException.class.getSimpleName());

        writer.emitAnnotation(Override.class);
        writer.beginMethod(classElement.getSimpleName().toString(),
                           "parseElement",
                           EnumSet.of(Modifier.PUBLIC),
                           parameters,
                           throwsTypes);

        writer.emitStatement("%s object = new %s()",
                             classElement.getSimpleName(),
                             classElement.getSimpleName());
        writer.emitStatement("parseAttributes(object, reader)");
        writer.emitStatement("parseChildren(object, reader)");

        writer.emitStatement(
                "Preconditions.checkState(reader.isEndElement(), \"Expected to be at an end "
                        + "element\")");
        writer.emitStatement("reader.next()");

        writer.beginControlFlow("if (reader.isCharacters())");
        writer.emitStatement("reader.nextTag()");
        writer.endControlFlow();

        ExecutableElement postParseMethod = findPostParseMethod();
        if (postParseMethod != null) {
            writer.emitStatement("object.%s()", postParseMethod.getSimpleName());
        }

        writer.emitStatement("return object");
        writer.endMethod();
    }

    private ExecutableElement findPostParseMethod() {
        ExecutableElement result = null;
        List<? extends Element> allMembers =
                processingEnv.getElementUtils().getAllMembers(classElement);
        for (Element e : allMembers) {
            if (e.getAnnotation(XmlPostParse.class) != null) {

                if (result != null) {
                    String errorMessage =
                            String.format("Found multiple methods annotated with @%s",
                                          XmlPostParse.class.getSimpleName());
                    processingEnv.getMessager()
                                 .printMessage(Diagnostic.Kind.ERROR, errorMessage, e);
                }

                result = (ExecutableElement) e;

                if (result.getParameters().size() > 0) {
                    String errorMessage =
                            String.format("A method annotated with @%s must take no parameters.",
                                          XmlPostParse.class.getSimpleName());
                    processingEnv.getMessager()
                                 .printMessage(Diagnostic.Kind.ERROR, errorMessage, result);
                }

                if (result.getModifiers().contains(Modifier.PRIVATE)) {
                    String errorMessage =
                            String.format("A method annotated with @%s must be non-private.",
                                          XmlPostParse.class.getSimpleName());
                    processingEnv.getMessager()
                                 .printMessage(Diagnostic.Kind.ERROR, errorMessage, result);
                }
            }
        }
        return result;
    }
}