#!/bin/bash
mvn clean compile assembly:single
cp target/soot-2017-01-24-cfallin-jar-with-dependencies.jar soot.jar
