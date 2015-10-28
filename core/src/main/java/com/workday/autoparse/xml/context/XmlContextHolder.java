/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.context;

import com.workday.autoparse.xml.parser.XmlStreamParser;

/**
 * A static accessor for the current context. The context is unique to a thread and will be set at
 * the start of {@link XmlStreamParser#parseStream(java.io.InputStream)} and unset at the completion
 * of the method.
 *
 * @author nathan.taylor
 * @since 2013-9-23
 */
public class XmlContextHolder {

    private static ThreadLocal<XmlParserContext> context = new ThreadLocal<XmlParserContext>();

    private XmlContextHolder() {
    }

    public static void setContext(XmlParserContext context) {
        XmlContextHolder.context.set(context);
    }

    public static void removeContext() {
        context.remove();
    }

    public static XmlParserContext getContext() {
        return context.get();
    }
}
