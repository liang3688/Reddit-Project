#!/usr/bin/env sh
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
