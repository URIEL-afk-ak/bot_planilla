#!/bin/bash
set -e

# Descargar Maven a /tmp (que debería ser escribible)
MAVEN_VER=3.9.6
MAVEN_DIR=/tmp/maven-$MAVEN_VER

if [ ! -d "$MAVEN_DIR" ]; then
  cd /tmp
  curl -L https://dlcdn.apache.org/maven/maven-3/$MAVEN_VER/binaries/apache-maven-$MAVEN_VER-bin.tar.gz -o maven.tar.gz
  tar -xzf maven.tar.gz
  mv apache-maven-$MAVEN_VER $MAVEN_DIR
fi

export PATH="$MAVEN_DIR/bin:$PATH"

# Ir al directorio del proyecto
cd /opt/render/project/src || cd .

# Ejecutar Maven
mvn clean package -DskipTests

