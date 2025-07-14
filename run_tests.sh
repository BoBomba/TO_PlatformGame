#!/bin/bash
set -e
mkdir -p build/classes build/test-classes

# Compile main sources (all .java files in project root)
javac -d build/classes $(find . -maxdepth 1 -name '*.java')

# Compile tests
javac -cp lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:build/classes -d build/test-classes $(find src/test/java -name '*.java')

# Run tests
java -cp build/classes:build/test-classes:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore CharacterMementoTest
