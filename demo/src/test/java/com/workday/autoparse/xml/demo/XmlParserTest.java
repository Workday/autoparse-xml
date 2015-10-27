/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.autoparse.xml.demo;

import com.workday.autoparse.xml.context.XmlParserSettings;
import com.workday.autoparse.xml.context.XmlParserSettingsBuilder;
import com.workday.autoparse.xml.demo.duplicatepartition.DuplicatePartitionedModel;
import com.workday.autoparse.xml.demo.partition.PartitionedModel;
import com.workday.autoparse.xml.parser.ParseException;
import com.workday.autoparse.xml.parser.UnexpectedChildException;
import com.workday.autoparse.xml.parser.UnknownElementException;
import com.workday.autoparse.xml.parser.XmlStreamParser;
import com.workday.autoparse.xml.parser.XmlStreamParserFactory;
import com.workday.autoparse.xml.utils.StringTransformer;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author nathan.taylor
 * @since 2013-9-20-14:42
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class XmlParserTest {

    static final double DOUBLE_E = 1E-6;
    static final float FLOAT_E = (float) 1E-6;

    @Test
    public void testParse()
            throws UnknownElementException, ParseException, UnexpectedChildException {

        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser();

        InputStream in = getInputStreamOf("input.xml");
        DemoModel model = (DemoModel) parser.parseStream(in);

        assertTrue(model.myBoxedBoolean);
        assertTrue(model.myPrimitiveBoolean);
        assertEquals(new BigDecimal(0.5), model.myBigDecimal);
        assertEquals(BigInteger.ONE, model.myBigInteger);
        assertEquals(2, model.myPrimitiveByte);
        assertEquals(Byte.valueOf((byte) 4), model.myBoxedByte);
        assertEquals('a', model.myPrimitiveChar);
        assertEquals(Character.valueOf('b'), model.myBoxedChar);
        assertEquals(5.5, model.myPrimitiveDouble, DOUBLE_E);
        assertEquals(Double.valueOf(6.5), model.myBoxedDouble);
        assertEquals(7.5f, model.myPrimitiveFloat, FLOAT_E);
        assertEquals(Float.valueOf(8.5f), model.myBoxedFloat);
        assertEquals(9, model.myPrimitiveInt);
        assertEquals(Integer.valueOf(10), model.myBoxedInt);
        assertEquals(11, model.myPrimitiveLong);
        assertEquals(Long.valueOf(12), model.myBoxedLong);
        assertEquals(13, model.myPrimitiveShort);
        assertEquals(Short.valueOf((short) 15), model.myBoxedShort);
        assertEquals("Bob", model.myString);
        assertTrue(model.myChildModel != null);

        assertEquals("My_String_Value", model.myChildModel.myString);
        assertEquals(5, model.myChildModel.myInt);

        assertEquals(3, model.repeatedChildModels.size());

        assertEquals("a", model.repeatedChildModels.get(0).value);
        assertEquals("I am some text.", model.repeatedChildModels.get(0).textContent);

        assertEquals("b", model.repeatedChildModels.get(1).value);
        assertNull(model.repeatedChildModels.get(1).textContent);

        assertEquals("c", model.repeatedChildModels.get(2).value);
    }

    @Test
    public void testAlternateAttributes()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser();
        InputStream in = getInputStreamOf("alternate-attribute-input.xml");

        RootModel root = (RootModel) parser.parseStream(in);
        assertEquals(4, root.children.size());
        assertEquals(1, ((AlternateAttributeModel) root.children.get(0)).anInt);
        assertEquals(2, ((AlternateAttributeModel) root.children.get(1)).anInt);
        assertEquals(3, ((AlternateAttributeModel) root.children.get(2)).anInt);
        assertEquals(2, ((AlternateAttributeModel) root.children.get(3)).anInt);
    }

    @Test
    public void testSetters()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser();
        InputStream in = getInputStreamOf("setter-input.xml");

        SetterModel setterModel = (SetterModel) parser.parseStream(in);
        assertEquals(5, setterModel.anInt);
        assertNotNull(setterModel.child);
        assertEquals(2, setterModel.repeatedChildren.size());
        // Pull autoparse includes newlines and whitespace, so get rid of it here.
        assertEquals("Hello",
                     StringUtils.trim(StringUtils.replace(setterModel.textContent, "\n", " ")));
    }

    @Test
    public void testOtherPackage()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser();
        InputStream in = getInputStreamOf("other-package-input.xml");

        ThisPackageModel root = (ThisPackageModel) parser.parseStream(in);
        assertNotNull(root.otherPackageModel);
    }

    @Test
    public void testPostParse()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser();
        InputStream in = getInputStreamOf("post-parse-input.xml");

        PostParseModel model = (PostParseModel) parser.parseStream(in);
        assertTrue(model.postParseCalled);
    }

    @Test
    public void testIgnoringUnknownElementsDoesNotThrowException()
            throws ParseException, UnexpectedChildException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.IGNORE)
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("unknown-elements-input.xml");

        RootModel rootModel = null;
        try {
            rootModel = (RootModel) parser.parseStream(in);
        } catch (UnknownElementException e) {
            fail(String.format("Got an %s: %s", e.getClass().getSimpleName(), e));
        }

        assertEquals(1, rootModel.children.size());
    }

    @Test
    public void testUnknownElementThrowsException()
            throws ParseException, UnexpectedChildException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.ERROR)
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("unknown-elements-input.xml");

        try {
            parser.parseStream(in);
            fail(String.format("Expected an %s.", UnknownElementException.class.getSimpleName()));
        } catch (UnknownElementException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Unknown_Model"));
        }
    }

    @Test
    public void testIgnoringUnexpectedChildDoesNotThrowException()
            throws ParseException, UnknownElementException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().ignoreUnexpectedChildren(true).build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("unexpected-child-input.xml");

        ChildModel model = null;
        try {
            model = (ChildModel) parser.parseStream(in);
        } catch (UnexpectedChildException e) {
            fail(String.format("Got an %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }

        assertEquals(1, model.myInt);
    }

    @Test
    public void testUnexpectedChildThrowsException()
            throws UnknownElementException, ParseException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().ignoreUnexpectedChildren(false).build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("unexpected-child-input.xml");

        try {
            parser.parseStream(in);
            fail(String.format("Expected an %s.", UnexpectedChildException.class.getSimpleName()));
        } catch (UnexpectedChildException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(RootModel.class.getCanonicalName()));
        }
    }

    @Test
    public void testUnknownElementCreatesUnknownModel()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.PARSE)
                                              .withUnknownElementClass(UnknownModel.class)
                                              .ignoreUnexpectedChildren(false)
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("unknown-elements-input.xml");

        RootModel rootModel = (RootModel) parser.parseStream(in);
        assertEquals(3, rootModel.children.size());

        UnknownModel child0 = (UnknownModel) rootModel.children.get(0);
        assertEquals(2, child0.children.size());
        assertTrue("child0.children.get(0) instanceof UnknownModel",
                   child0.children.get(0) instanceof UnknownModel);
        assertTrue("child0.children.get(1) instanceof ChildModel",
                   child0.children.get(1) instanceof ChildModel);

        assertTrue("rootModel.children.get(1) instanceof UnknownModel",
                   rootModel.children.get(1) instanceof UnknownModel);

        assertTrue("rootModel.children.get(2) instanceof ChildModel",
                   rootModel.children.get(2) instanceof ChildModel);
    }

    @Test
    public void testStringFilters()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        StringTransformer newLineFilter = new StringTransformer() {
            @Override
            public String transform(String input) {
                return input.replace("&#xa;", "\n");
            }
        };
        StringTransformer hFilter = new StringTransformer() {
            @Override
            public String transform(String input) {
                return input.replace('H', 'J');
            }
        };
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().addFilter(newLineFilter).addFilter(hFilter).build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("unfiltered-input.xml");

        RepeatedChildModel model = (RepeatedChildModel) parser.parseStream(in);
        String expected = "Jello\nJello\nJello";
        assertEquals(expected, model.value);
        assertEquals(expected, model.textContent);
    }

    @Test
    public void testMissingAttributesAreNotSet()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser();
        InputStream in = getInputStreamOf("missing-attributes.xml");

        DemoModel model = (DemoModel) parser.parseStream(in);

        assertTrue(model.myBoxedBoolean);
        assertTrue(model.myPrimitiveBoolean);
        assertEquals(BigDecimal.ONE, model.myBigDecimal);
        assertEquals(BigInteger.TEN, model.myBigInteger);
        assertEquals(-1, model.myPrimitiveByte);
        assertEquals(Byte.valueOf((byte) -1), model.myBoxedByte);
        assertEquals('a', model.myPrimitiveChar);
        assertEquals(Character.valueOf('a'), model.myBoxedChar);
        assertEquals(-1.0, model.myPrimitiveDouble, DOUBLE_E);
        assertEquals(Double.valueOf(-1.0), model.myBoxedDouble);
        assertEquals(-1f, model.myPrimitiveFloat, FLOAT_E);
        assertEquals(Float.valueOf(-1f), model.myBoxedFloat);
        assertEquals(-1, model.myPrimitiveInt);
        assertEquals(Integer.valueOf(-1), model.myBoxedInt);
        assertEquals(-1, model.myPrimitiveLong);
        assertEquals(Long.valueOf(-1), model.myBoxedLong);
        assertEquals(-1, model.myPrimitiveShort);
        assertEquals(Short.valueOf((short) -1), model.myBoxedShort);
        assertEquals("default", model.myString);
        assertEquals("default", model.myTextContent);
    }

    @Test
    public void testPartitionedPackageModelNotFoundWhenNotIncluded()
            throws ParseException, UnexpectedChildException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.ERROR)
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("partitioned-model.xml");
        boolean exceptionCaught = false;
        try {
            parser.parseStream(in);
        } catch (UnknownElementException ignored) {
            exceptionCaught = true;
        }

        assertTrue("exception caught", exceptionCaught);
    }

    @Test
    public void testPartitionedPackageModelFoundWhenIncluded()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.ERROR)
                                              .withPartitions(PartitionedModel.class.getPackage()
                                                                                    .getName())
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("partitioned-model.xml");
        PartitionedModel partitionedModel = (PartitionedModel) parser.parseStream(in);

        assertEquals("a string", partitionedModel.string);
    }

    @Test
    public void testDefaultNotFoundWhenNotIncluded()
            throws ParseException, UnexpectedChildException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.ERROR)
                                              .withPartitions(PartitionedModel.class.getPackage()
                                                                                    .getName())
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("input.xml");
        boolean exceptionCaught = false;
        try {
            parser.parseStream(in);
        } catch (UnknownElementException ignored) {
            exceptionCaught = true;
        }

        assertTrue("exception caught", exceptionCaught);
    }

    @Test
    public void testMultiplePackages()
            throws ParseException, UnexpectedChildException, UnknownElementException {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withUnknownElementHandling(XmlParserSettings
                                                                                  .UnknownElementHandling.PARSE)
                                              .withUnknownElementClass(UnknownModel.class)
                                              .withPartitions(PartitionedModel.class.getPackage()
                                                                                    .getName(),
                                                              XmlParserSettingsBuilder
                                                                      .DEFAULT_PACKAGE)
                                              .build();
        XmlStreamParser parser = XmlStreamParserFactory.newXmlStreamParser(settings);
        InputStream in = getInputStreamOf("multiple-partitions.xml");

        RootModel rootModel = (RootModel) parser.parseStream(in);

        assertEquals(3, rootModel.children.size());
        assertTrue(rootModel.children.get(0) instanceof ThisPackageModel);
        assertNotNull(((ThisPackageModel) rootModel.children.get(0)).otherPackageModel);
        assertTrue(rootModel.children.get(1) instanceof PartitionedModel);
        assertTrue(rootModel.children.get(2) instanceof UnknownModel);
    }

    @Test
    public void testDuplicateMappingInSeparateRootPackagesThrowsException() {
        XmlParserSettings settings =
                new XmlParserSettingsBuilder().withPartitions(PartitionedModel.class.getPackage()
                                                                                    .getName(),
                                                              DuplicatePartitionedModel.class
                                                                      .getPackage().getName())
                                              .build();
        boolean exceptionCaught = false;
        try {
            XmlStreamParserFactory.newXmlStreamParser(settings);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
        }
        assertTrue("Exception caught.", exceptionCaught);
    }

    private InputStream getInputStreamOf(String fileName) {
        return new BufferedInputStream(XmlParserTest.class.getResourceAsStream(fileName));
    }
}
