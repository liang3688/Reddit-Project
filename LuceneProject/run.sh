#!/usr/bin/env sh
mvn clean compile assembly:single
java -jar target/LuceneProject-1.0-SNAPSHOT-jar-with-dependencies.jar $1 $2 $3 $4
