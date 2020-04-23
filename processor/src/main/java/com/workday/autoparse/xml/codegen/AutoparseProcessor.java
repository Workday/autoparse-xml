/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlElement;
import com.workday.autoparse.xml.annotations.XmlPostParse;
import com.workday.autoparse.xml.annotations.XmlTextContent;
import com.workday.autoparse.xml.annotations.XmlUnknownElement;
import com.workday.autoparse.xml.utils.CollectionUtils;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * A {@link javax.annotation.processing.Processor} that generates code used to inflate objects
 * annotated with {@link XmlElement}. This processor will be invoked during compilation. Clients
 * will have no need to instantiate or make calls to this class.
 *
 * @author nathan.taylor
 * @since 2013-09-30
 */
public class AutoparseProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        // Generate parsers for classes annotated with @XmlElement
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XmlElement.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
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

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return CollectionUtils.newHashSet(XmlElement.class.getCanonicalName(),
                                          XmlUnknownElement.class.getCanonicalName(),
                                          XmlPostParse.class.getCanonicalName(),
                                          XmlChildElement.class.getCanonicalName(),
                                          XmlAttribute.class.getCanonicalName(),
                                          XmlTextContent.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generateClassParser(TypeElement element) {
        try {
            new XmlElementParserGenerator(processingEnv, element).generateParser();
        } catch (IOException e) {
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
        }
    }

}
