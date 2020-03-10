#!/bin/sh

VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version | grep -Ev '(^\[|Download\w+:)'`

mvn package
mkdir jhin3-$VERSION
cp target/jhin3-$VERSION-jar-with-dependencies.jar jhin3-$VERSION/jhin3-$VERSION.jar
cp LICENSE jhin3-$VERSION/
cp NOTICE jhin3-$VERSION/
cd manual && make && cd ..
cp manual/Jhin3\ user\ manual.pdf jhin3-$VERSION/
cp sample_conf.json jhin3-$VERSION/
zip jhin3-$VERSION.zip -r jhin3-$VERSION
rm -r jhin3-$VERSION
