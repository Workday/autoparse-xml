/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.squareup.javawriter.JavaWriter;
import com.workday.autoparse.xml.context.XmlParserSettingsBuilder;
import com.workday.autoparse.xml.parser.GeneratedClassNames;
import com.workday.autoparse.xml.parser.ParserMap;
import com.workday.autoparse.xml.parser.XmlElementParser;
import com.workday.meta.Modifiers;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * @author nathan.taylor
 * @since 2013-9-17
 */
class ParserMapGenerator {

    private static final String MAP_TYPE =
            String.format("Map<String, %s<?>>", XmlElementParser.class.getSimpleName());

    private final ProcessingEnvironment processingEnv;
    private final PackageElement packageElement;
    private final Map<String, TypeElement> parseMap;

    ParserMapGenerator(ProcessingEnvironment processingEnv, PackageElement packageElement,
                       Map<String, TypeElement> parseMap) {
        this.processingEnv = processingEnv;
        this.packageElement = packageElement;
        this.parseMap = parseMap;
    }

    public void generateParseMap() throws IOException {
        String packageName = packageElement != null
                             ? packageElement.getQualifiedName().toString()
                             : XmlParserSettingsBuilder.DEFAULT_PACKAGE;

        String parserMapClassName = GeneratedClassNames.CLASS_GENERATED_PARSER_MAP;
        String qualifiedClassName = GeneratedClassNames.getQualifiedName(packageName,
                                                                         parserMapClassName);

        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(qualifiedClassName, parseMap.values().toArray(new Element[parseMap.size()]));

        JavaWriter writer = new JavaWriter(sourceFile.openWriter());
        writer.emitPackage(packageName);

        writer.emitImports(getParsableClassImports());
        writer.emitEmptyLine();
        writer.emitImports(getJavaImports());
        writer.emitEmptyLine();

        writer.beginType(GeneratedClassNames.CLASS_GENERATED_PARSER_MAP,
                         "class",
                         EnumSet.of(Modifier.PUBLIC, Modifier.FINAL),
                         null,
                         ParserMap.class.getCanonicalName());
        writer.emitEmptyLine();

        writeMapField(writer);
        writer.emitEmptyLine();
        writeGetter(writer);
        writer.emitEmptyLine();
        writeKeySet(writer);

        writer.endType();
        writer.close();
    }

    private Set<String> getParsableClassImports() {
        Set<String> results = new HashSet<>();
        for (TypeElement element : parseMap.values()) {
            results.add(element.getQualifiedName().toString());
        }
        return results;
    }

    private Set<String> getJavaImports() {
        Set<String> results = new HashSet<>();
        results.add(Map.class.getCanonicalName());
        results.add(HashMap.class.getCanonicalName());
        if (packageElement != null) {
            results.add(ParserMap.class.getCanonicalName());
            results.add(XmlElementParser.class.getCanonicalName());
        }
        return results;
    }

    private void writeMapField(JavaWriter writer) throws IOException {
        writer.emitField(MAP_TYPE, "MAP", Modifiers.PRIVATE_CONSTANT,
                         String.format("new HashMap<String, %s<?>>()",
                                       XmlElementParser.class.getSimpleName()));

        writer.beginInitializer(true);
        for (Map.Entry<String, TypeElement> entry : parseMap.entrySet()) {
            writer.emitStatement("MAP.put(\"%s\", %s.INSTANCE)", entry.getKey(),
                                 entry.getValue().getSimpleName());
        }
        writer.endInitializer();
    }

    private void writeGetter(JavaWriter writer) throws IOException {

        writer.emitAnnotation(Override.class);
        writer.beginMethod(JavaWriter.type(XmlElementParser.class, "?"),
                           "get",
                           EnumSet.of(Modifier.PUBLIC),
                           "String",
                           "name");
        writer.emitStatement("return MAP.get(name)");
        writer.endMethod();
    }

    private void writeKeySet(JavaWriter writer) throws IOException {

        writer.emitAnnotation(Override.class);
        writer.beginMethod(JavaWriter.type(Set.class, "String"), "keySet", Modifiers.PUBLIC);
        writer.emitStatement("return MAP.keySet()");
        writer.endMethod();
    }

}
