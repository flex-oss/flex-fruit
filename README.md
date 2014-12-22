flex-fruit
==========

[![Build Status](https://travis-ci.org/flex-oss/flex-fruit.png?branch=master)](https://travis-ci.org/flex-oss/flex-fruit) [![Coverage Status](https://coveralls.io/repos/flex-oss/flex-fruit/badge.png)](https://coveralls.io/r/flex-oss/flex-fruit) 

Fruit is a simple object-store API layer that abstracts common DAO-type persistence use-cases. It provides a basic
implementation for JPA that allows to kickstart simple persistence solutions.

The name comes from the first letters of *OB*ject *ST*ore which means 'fruit' in german.

License
-------

Fruit is is distributed under the terms of the Apache Software Foundation license, version 2.0. The text is included in
the file LICENSE in the root of the project.


Components
----------

* `fruit-core` contains the Fruit API
* `fruit-jpa` is an implementation of Fruit using JPA
* `fruit-util` contains useful utilities, e.g. a Fruit Repository implementation using Maps

Usage
-----

Fruit is hosted on maven-central and can easily be integrated into your project using maven

### API

Maybe you want to separate your implementation from the API usage. If you only need the api, add the `fruit-core`
dependency

    <dependency>
        <groupId>org.cdlflex</groupId>
        <artifactId>fruit-core</artifactId>
        <version>add current version here</version>
    </dependency>

### JPA

If you want to use Fruit with a JPA implementation, you would add the `fruit-jpa` dependency

    <dependency>
        <groupId>org.cdlflex</groupId>
        <artifactId>fruit-jpa</artifactId>
        <version>add current version here</version>
    </dependency>

Building
--------

Build the entire project using Maven

    mvn clean install
