/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlParserPartition;
import com.workday.autoparse.xml.annotations.XmlPostParse;
import com.workday.autoparse.xml.annotations.XmlTextContent;
import com.workday.autoparse.xml.annotations.XmlUnknownElement;
import com.workday.meta.PackageTree;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * A {@link javax.annotation.processing.Processor} that generates code used to inflate objects
 * annotated with {@link XmlElement}. This processor will be invoked during compilation. Clients
 * will have no need to instantiate or make calls to this class.
 *
 * @author nathan.taylor
 * @since 2013-09-30
 */
public class AutoParseProcessor extends AbstractProcessor {

    private Map<String, Collection<TypeElement>> parserMap = Maps.newHashMap();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        // Generate parsers for classes annotated with @XmlElement
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XmlElement.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
                addClassToParseMap((TypeElement) element);
                generateClassParser((TypeElement) element);
            }
        }

        // Generate parsers for classes annotated with @XmlUnknownElement
        Set<? extends Element> elementsForUnknown =
                roundEnv.getElementsAnnotatedWith(XmlUnknownElement.class);
        for (Element element : elementsForUnknown) {
            if (element.getAnnotation(XmlElement.class) == null) {
                generateClassParser((TypeElement) element);
            }
        }

        // Generate ParserMaps
        Set<PackageElement> partitionPackageElements = ElementFilter.packagesIn(
                roundEnv.getElementsAnnotatedWith(XmlParserPartition.class));
        PackageTree packageTree =
                new PackageTree(processingEnv.getElementUtils(), partitionPackageElements);

        generateParserMaps(packageTree);
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(XmlElement.class.getCanonicalName(),
                               XmlUnknownElement.class.getCanonicalName(),
                               XmlPostParse.class.getCanonicalName(),
                               XmlChildElement.class.getCanonicalName(),
                               XmlAttribute.class.getCanonicalName(),
                               XmlTextContent.class.getCanonicalName(),
                               XmlParserPartition.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void addClassToParseMap(TypeElement element) {
        XmlElement annotation = element.getAnnotation(XmlElement.class);
        for (String parseKey : annotation.value()) {
            if (StringUtils.isNotEmpty(parseKey)) {
                putInCollectionMap(parserMap, parseKey, element);
            }
        }
    }

    private <K, V> void putInCollectionMap(Map<K, Collection<V>> map, K key, V value) {
        Collection<V> collection = map.get(key);
        if (collection == null) {
            collection = new ArrayList<>();
            map.put(key, collection);
        }
        collection.add(value);
    }

    private void generateClassParser(TypeElement element) {
        try {
            new XmlElementParserGenerator(processingEnv, element).generateParser();
        } catch (IOException e) {
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
        }

    }

    private void generateParserMaps(PackageTree packageTree) {
        Map<PackageElement, Map<String, TypeElement>> mapsByPackage = splitParserMap(packageTree);
        for (Map.Entry<PackageElement, Map<String, TypeElement>> entry : mapsByPackage.entrySet()) {
            ParserMapGenerator generator =
                    new ParserMapGenerator(processingEnv, entry.getKey(), entry.getValue());
            try {
                generator.generateParseMap();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }
    }

    private Map<PackageElement, Map<String, TypeElement>> splitParserMap(PackageTree packageTree) {
        Map<PackageElement, Map<String, TypeElement>> mapsByPackage = new HashMap<>();
        for (Map.Entry<String, Collection<TypeElement>> entry : parserMap.entrySet()) {
            String parseKey = entry.getKey();
            for (TypeElement element : entry.getValue()) {
                // A null matching package means that this element goes into the default partition,
                // which is keyed by null here.
                PackageElement matchingPackage = packageTree.getMatchingPackage(element);
                Map<String, TypeElement> map = mapsByPackage.get(matchingPackage);
                if (map == null) {
                    map = new HashMap<>();
                    mapsByPackage.put(matchingPackage, map);
                }
                TypeElement previousValue = map.put(parseKey, element);
                if (previousValue != null) {
                    String packageString = matchingPackage != null
                                           ? String.format(Locale.US,
                                                           "partition under package '%s'",
                                                           matchingPackage.getQualifiedName())
                                           : "the default partition";
                    String errorMessage =
                            String.format("%s and %s both tried to map to tag name \"%s\" in %s.",
                                          element.getQualifiedName(),
                                          previousValue.getQualifiedName(),
                                          parseKey,
                                          packageString);
                    processingEnv.getMessager()
                                 .printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
                }
            }
        }
        return mapsByPackage;
    }

}
