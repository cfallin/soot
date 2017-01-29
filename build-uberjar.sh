#!/bin/bash
mvn clean compile assembly:single
cp target/soot-*-jar-with-dependencies.jar soot.jar
