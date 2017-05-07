# autoparse-xml

Autoparse XML is a java library built specifically for Android that uses code generation to parse XML into custom objects in your project.

Learn how to use Autoparse XML in the [wiki](https://github.com/workday/autoparse-xml/wiki)!

**Latest Version:**  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.workday/autoparse-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.workday/autoparse-xml)

**Build Status:** [![Build Status](https://travis-ci.org/Workday/autoparse-xml.svg?branch=master)](https://travis-ci.org/Workday/autoparse-xml)

## Installation

Add the following lines to your `build.gradle` file, replacing `$autoparse_xml_version` with latest version from Maven Central.

```
repositories {
    mavenCentral()
}

dependencies {
    compile "com.workday:autoparse-xml:$autoparse_xml_version"
    compile "com.workday:autoparse-xml-processor:$autoparse_xml_version"
}
```

Note that if you use the [android-apt plugin](https://bitbucket.org/hvisser/android-apt) or the [kotlin-android plugin](https://kotlinlang.org/docs/reference/using-gradle.html), you may use `apt` or `kapt` respectively instead of `compile` for `autoparse-xml-processor`, e.g.

```
apt "com.workday:autoparse-xml-processor:$autoparse_xml_version"
```
In fact, it is highly recommended that you use `apt` or `kapt` as this will get rid of some "invalid package" and related warnings.
