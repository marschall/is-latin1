#!/bin/sh

${JAVA_HOME}/bin/java \
  -Xmx256m \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:+AlwaysPreTouch \
  --enable-preview \
  --module-path target/is-latin1-1.0.0-SNAPSHOT.jar \
  --module com.github.marschall.islatinone \
  /tmp/ISO885918727130585629604208.txt
