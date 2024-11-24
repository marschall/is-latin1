#!/bin/sh

${JAVA_HOME}/bin/java \
  -Xmx100m \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC \
  --enable-preview \
  -jar target/is-latin1-1.0.0-SNAPSHOT.jar \
  /tmp/ISO885913611086970171486915.txt
