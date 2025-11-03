#!/bin/bash
#
# Gradle start up script for POSIX (Linux/macOS)
#

if [ "$(uname)" = "Darwin" ] ; then
    APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P )
    APP_NAME="Gradle"
    JAVACMD="${JAVA_HOME}/bin/java"
else
    APP_HOME="${APP_HOME:-./}"
    APP_NAME="Gradle"
    JAVACMD="java"
fi

# Maximum file descriptors
MAX_FD=maximum

warn () {
    echo "$*" >&2
}

die () {
    echo >&2
    echo "$*" >&2
    echo >&2
    exit 1
}

# OS specific support
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN* )         cygwin=true ;;
  Darwin* )         darwin=true ;;
  MSYS*|MINGW* )    msys=true ;;
  NONSTOP* )        nonstop=true ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine Java command
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

# Increase maximum file descriptors
if ! $cygwin && ! $msys ; then
    case $- in
      *i*) ;; 
      *) set +o allexec ;;
    esac
    ulimit -n "$MAX_FD" || warn "Could not set maximum file descriptor limit to $MAX_FD"
fi

# Escape application args
save () {
    for i do
        printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/"
    done
    echo " "
}

APP_ARGS=$(save "$@")
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
APP_HOME=$APP_HOME exec "$JAVACMD" \
  -Dorg.gradle.appname="$APP_NAME" \
  -Dorg.gradle.wrapper.properties="$APP_HOME/gradle/wrapper/gradle-wrapper.properties" \
  -cp "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$APP_ARGS"