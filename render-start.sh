#!/bin/bash

# Configurar Java (mismo proceso que en el build)
if [ -z "$JAVA_HOME" ]; then
  # Buscar Java en ubicaciones comunes
  if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
  elif [ -d "/usr/lib/jvm/java-17-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
  elif [ -d "/usr/lib/jvm/java-11-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
  elif [ -d "/usr/lib/jvm/java-11-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-11-openjdk"
  else
    # Buscar Java descargado en /tmp
    JDK_DIR="/tmp/jdk-17.0.10"
    if [ -d "$JDK_DIR" ] && [ -f "$JDK_DIR/bin/java" ]; then
      export JAVA_HOME="$JDK_DIR"
    else
      # Buscar cualquier JDK en /tmp
      for dir in /tmp/jdk-* /tmp/java-*; do
        if [ -d "$dir" ] && [ -f "$dir/bin/java" ]; then
          export JAVA_HOME="$dir"
          break
        fi
      done
    fi
  fi
fi

if [ -z "$JAVA_HOME" ]; then
  echo "Error: Java no encontrado"
  exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"

# Ejecutar la aplicación
java -jar target/bot-planilla-backend-1.0.0.jar --spring.profiles.active=prod

