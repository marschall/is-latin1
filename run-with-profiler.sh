#!/bin/sh

set -e
set -u

${JAVA_HOME}/bin/java \
  -Xmx256m \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:+AlwaysPreTouch \
  -agentpath:${HOME}/bin/async-profiler/async-profiler-3.0-linux-x64/lib/libasyncProfiler.so=start,alloc=100k,file=async-profiler-memory.jfr \
  --enable-preview \
  --module-path target/is-latin1-1.0.0-SNAPSHOT.jar \
  --module com.github.marschall.islatinone \
  /tmp/ISO885912046423853045236867.txt
