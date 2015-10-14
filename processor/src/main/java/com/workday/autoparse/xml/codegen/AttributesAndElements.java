/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.codegen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workday.autoparse.xml.annotations.XmlAttribute;
import com.workday.autoparse.xml.annotations.XmlChildElement;
import com.workday.autoparse.xml.annotations.XmlTextContent;
import com.workday.meta.MetaTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author nathan.taylor
 * @since 2013-10-11
 */
class AttributesAndElements {

    private final ProcessingEnvironment processingEnv;
    private final TypeElement classElement;
    private Map<List<String>, Element> attributes = Maps.newHashMap();
    private Collection<VariableElement> singletonFieldChildren = Sets.newHashSet();
    private Collection<VariableElement> collectionFieldChildren = Sets.newHashSet();
    private Collection<ExecutableElement> singletonSetterChildren = Sets.newHashSet();
    private Collection<ExecutableElement> collectionSetterChildren = Sets.newHashSet();
    private Element textContentElement;
    private MetaTypes metaTypes;

    public AttributesAndElements(ProcessingEnvironment processingEnv, TypeElement classElement) {
        this.processingEnv = processingEnv;
        metaTypes = new MetaTypes(processingEnv);
        this.classElement = classElement;
        findAttributesAndElements();
    }

    public Map<List<String>, Element> getAttributes() {
        return attributes;
    }

    public Collection<VariableElement> getSingletonFieldChildren() {
        return singletonFieldChildren;
    }

    public Collection<VariableElement> getCollectionFieldChildren() {
        return collectionFieldChildren;
    }

    public Collection<ExecutableElement> getSingletonSetterChildren() {
        return singletonSetterChildren;
    }

    public Collection<ExecutableElement> getCollectionSetterChildren() {
        return collectionSetterChildren;
    }

    public Element getTextContentElement() {
        return textContentElement;
    }

    private void findAttributesAndElements() {

        Map<String, Element> visitedAttributes = Maps.newHashMap();
        Map<TypeMirror, Element> visitedElements = Maps.newHashMap();

        for (Element e : processingEnv.getElementUtils().getAllMembers(classElement)) {

            XmlAttribute attributeAnnotation = e.getAnnotation(XmlAttribute.class);
            XmlChildElement elementAnnotation = e.getAnnotation(XmlChildElement.class);
            XmlTextContent textContentAnnotation = e.getAnnotation(XmlTextContent.class);
            int numAnnotationsPresent =
                    numAnnotationsPresent(attributeAnnotation,
                                          elementAnnotation,
                                          textContentAnnotation);

            if (numAnnotationsPresent > 1) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                         "An XML object can only be parsed as one"
                                                                 + " type.",
                                                         e);
            }

            if (attributeAnnotation != null) {
                assignAsAttribute(e, attributeAnnotation, visitedAttributes);
            } else if (textContentAnnotation != null) {
                assignAsTextContent(e);
            } else if (elementAnnotation != null) {
                assignAsElement(e, visitedElements);
            }
        }
    }

    private void assignAsAttribute(Element e,
                                   XmlAttribute annotation,
                                   Map<String, Element> visitedAttributes) {
        for (String name : annotation.value()) {
            if (visitedAttributes.containsKey(name)) {
                Element existingAttribute = visitedAttributes.get(name);
                TypeElement enclosingClass = (TypeElement) existingAttribute.getEnclosingElement();
                String currentAttributeName =
                        String.format("%s.%s",
                                      enclosingClass.getQualifiedName(),
                                      existingAttribute.getSimpleName());
                String errorMessage =
                        String.format("%s also maps to attribute name \"%s\"",
                                      currentAttributeName,
                                      name);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, e);
            } else {
                visitedAttributes.put(name, e);
            }
        }
        attributes.put(Lists.newArrayList(annotation.value()), e);
    }

    private void assignAsTextContent(Element e) {

        if (textContentElement != null) {
            String errorMessage = String.format(
                    "Only one field or one setter can be annotated with @%s",
                    XmlTextContent.class.getSimpleName());
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, e);
            return;
        }

        TypeMirror type;
        if (e instanceof ExecutableElement) {
            // This is a setter
            ExecutableElement method = (ExecutableElement) e;
            if (!assertMethodHasSingleParameter(method)) {
                return;
            }

            type = method.getParameters().get(0).asType();
        } else {
            // This is a field
            type = e.asType();
        }

        if (!metaTypes.isString(type)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(
                    "A field or setter annotated with %s must take type java.lang.String",
                    XmlTextContent.class.getSimpleName()));
            return;
        }

        textContentElement = e;
    }

    private void assignAsElement(Element e, Map<TypeMirror, Element> visitedElements) {
        if (e instanceof ExecutableElement) {
            // This is a setter
            ExecutableElement method = (ExecutableElement) e;
            if (!assertMethodHasSingleParameter(method)) {
                return;
            }

            DeclaredType parameterType = (DeclaredType) method.getParameters().get(0).asType();

            if (!assertElementTypeNotAlreadyVisited(parameterType, e, visitedElements)) {
                return;
            }

            if (metaTypes.isAssignable(parameterType, Collection.class)) {
                collectionSetterChildren.add(method);
            } else {
                singletonSetterChildren.add(method);
            }
        } else {
            // This is a field
            TypeMirror fieldType = e.asType();
            if (!assertElementTypeNotAlreadyVisited(fieldType, e, visitedElements)) {
                return;
            }

            if (metaTypes.isAssignable(fieldType, Collection.class)) {
                collectionFieldChildren.add((VariableElement) e);
            } else {
                singletonFieldChildren.add((VariableElement) e);
            }
        }
    }

    private int numAnnotationsPresent(XmlAttribute attributeAnnotation,
                                      XmlChildElement elementAnnotation,
                                      XmlTextContent textContentAnnotation) {
        int count = 0;
        if (attributeAnnotation != null) {
            count++;
        }
        if (elementAnnotation != null) {
            count++;
        }
        if (textContentAnnotation != null) {
            count++;
        }
        return count;
    }

    private boolean assertMethodHasSingleParameter(ExecutableElement method) {
        if (method.getParameters().size() != 1) {
            String errorMessage = "AutoParse can only set values on single-parameter methods.";
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, method);
            return false;
        }
        return true;
    }

    private boolean assertElementTypeNotAlreadyVisited(TypeMirror type, Element offendingElement,
                                                       Map<TypeMirror, Element> visitedElements) {
        if (visitedElements.containsKey(type)) {
            String currentElementName = visitedElements.get(type).getSimpleName().toString();
            String errorMessage =
                    String.format("%s also takes elements of type %s", currentElementName,
                                  type.toString());
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR, errorMessage, offendingElement);
            return false;
        }
        return true;
    }
}
