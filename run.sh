#!/bin/sh

${JAVA_HOME}/bin/java \
  -Xmx256m \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:+AlwaysPreTouch \
  --enable-preview \
  --add-modules jdk.incubator.vector \
  -jar target/is-latin1-1.0.0-SNAPSHOT.jar \
  /tmp/ISO885918727130585629604208.txt
