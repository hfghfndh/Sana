#!/bin/sh
# Gradle wrapper launcher (Gradle 9.2.1).
# If gradle/wrapper/gradle-wrapper.jar is missing, it is downloaded first.
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$JAR" ]; then
  URL="https://raw.githubusercontent.com/gradle/gradle/v9.2.1/gradle/wrapper/gradle-wrapper.jar"
  echo "Downloading gradle-wrapper.jar ..."
  if command -v curl >/dev/null 2>&1; then curl -fsSL "$URL" -o "$JAR"
  elif command -v wget >/dev/null 2>&1; then wget -q "$URL" -O "$JAR"
  else echo "Need curl or wget to download $URL" >&2; exit 1; fi
fi
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then JAVACMD="$JAVA_HOME/bin/java"; else JAVACMD=java; fi
exec "$JAVACMD" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew \
  -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
