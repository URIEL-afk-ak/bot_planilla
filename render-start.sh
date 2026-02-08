#!/bin/bash

echo "=== Iniciando aplicación ==="

# Configurar Java
if [ -z "$JAVA_HOME" ]; then
  echo "=== Buscando Java ==="
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

# Si Java no se encontró, descargarlo
if [ -z "$JAVA_HOME" ] || [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "=== Java no encontrado, descargando JDK portable ==="
  cd /tmp
  
  JDK_VER=17.0.10
  JDK_DIR=/tmp/jdk-$JDK_VER
  
  if [ ! -d "$JDK_DIR" ] || [ ! -f "$JDK_DIR/bin/java" ]; then
    echo "Descargando OpenJDK 17..."
    
    # Intentar múltiples URLs de Java
    JDK_URLS=(
      "https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz"
      "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.10_9.tar.gz"
      "https://cdn.azul.com/zulu/bin/zulu17.48.15-ca-jdk17.0.10-linux_x64.tar.gz"
    )
    
    DOWNLOADED_JDK=0
    for JDK_URL in "${JDK_URLS[@]}"; do
      echo "Intentando: $JDK_URL"
      if curl -L --max-time 60 --connect-timeout 15 "$JDK_URL" -o jdk.tar.gz 2>/dev/null; then
        FILE_SIZE=$(stat -c%s jdk.tar.gz 2>/dev/null || echo 0)
        if [ $FILE_SIZE -gt 1000000 ]; then  # Al menos 1MB
          echo "Java descargado correctamente ($FILE_SIZE bytes)"
          DOWNLOADED_JDK=1
          break
        else
          echo "Archivo muy pequeño, intentando siguiente URL..."
          rm -f jdk.tar.gz
        fi
      fi
    done
    
    if [ $DOWNLOADED_JDK -eq 1 ]; then
      echo "Extrayendo Java..."
      tar -xzf jdk.tar.gz
      # Buscar el directorio extraído
      for dir in jdk-* jdk* java-*; do
        if [ -d "$dir" ] && [ -f "$dir/bin/java" ]; then
          mv "$dir" $JDK_DIR 2>/dev/null || true
          break
        fi
      done
      rm -f jdk.tar.gz
    fi
  fi
  
  if [ -d "$JDK_DIR" ] && [ -f "$JDK_DIR/bin/java" ]; then
    export JAVA_HOME="$JDK_DIR"
    echo "JDK disponible en: $JAVA_HOME"
  else
    echo "Error: No se pudo descargar o encontrar Java"
    exit 1
  fi
fi

export PATH="$JAVA_HOME/bin:$PATH"

echo "=== JAVA_HOME: $JAVA_HOME ==="
echo "=== Verificando Java ==="
java -version

echo "=== Ejecutando aplicación ==="
java -jar target/bot-planilla-backend-1.0.0.jar --spring.profiles.active=prod

