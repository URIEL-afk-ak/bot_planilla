#!/bin/bash
set -e

echo "=== Iniciando build ==="

# Descargar Maven a /tmp
MAVEN_VER=3.9.6
MAVEN_DIR=/tmp/maven-$MAVEN_VER

if [ ! -d "$MAVEN_DIR" ]; then
  echo "=== Descargando Maven ==="
  cd /tmp
  
  # Intentar múltiples URLs y métodos
  MAVEN_URL1="https://dlcdn.apache.org/maven/maven-3/$MAVEN_VER/binaries/apache-maven-$MAVEN_VER-bin.tar.gz"
  MAVEN_URL2="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VER/binaries/apache-maven-$MAVEN_VER-bin.tar.gz"
  
  DOWNLOADED=0
  
  # Intentar con wget
  if command -v wget &> /dev/null; then
    echo "Intentando descargar con wget (URL 1)..."
    wget -q --timeout=30 "$MAVEN_URL1" -O maven.tar.gz && DOWNLOADED=1 || true
    
    if [ $DOWNLOADED -eq 0 ]; then
      echo "Intentando descargar con wget (URL 2)..."
      wget -q --timeout=30 "$MAVEN_URL2" -O maven.tar.gz && DOWNLOADED=1 || true
    fi
  fi
  
  # Si wget falló, intentar con curl
  if [ $DOWNLOADED -eq 0 ] && command -v curl &> /dev/null; then
    echo "Intentando descargar con curl (URL 1)..."
    curl -L --max-time 30 --connect-timeout 10 "$MAVEN_URL1" -o maven.tar.gz && DOWNLOADED=1 || true
    
    if [ $DOWNLOADED -eq 0 ]; then
      echo "Intentando descargar con curl (URL 2)..."
      curl -L --max-time 30 --connect-timeout 10 "$MAVEN_URL2" -o maven.tar.gz && DOWNLOADED=1 || true
    fi
  fi
  
  # Verificar que el archivo se descargó correctamente
  if [ ! -f maven.tar.gz ] || [ $(stat -f%z maven.tar.gz 2>/dev/null || stat -c%s maven.tar.gz 2>/dev/null || echo 0) -lt 1000 ]; then
    echo "Error: No se pudo descargar Maven correctamente"
    exit 1
  fi
  
  echo "=== Extrayendo Maven ==="
  tar -xzf maven.tar.gz
  mv apache-maven-$MAVEN_VER $MAVEN_DIR
  rm -f maven.tar.gz
fi

export PATH="$MAVEN_DIR/bin:$PATH"

# Configurar JAVA_HOME si no está definido
if [ -z "$JAVA_HOME" ]; then
  echo "=== Buscando Java ==="
  # Intentar encontrar Java en ubicaciones comunes
  if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
  elif [ -d "/usr/lib/jvm/java-17-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
  elif [ -d "/usr/lib/jvm/java-11-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
  elif [ -d "/usr/lib/jvm/java-11-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-11-openjdk"
  else
    # Buscar Java usando which
    JAVA_PATH=$(which java 2>/dev/null || echo "")
    if [ -n "$JAVA_PATH" ]; then
      JAVA_HOME=$(readlink -f "$JAVA_PATH" | sed "s:bin/java::")
      export JAVA_HOME
    fi
  fi
fi

if [ -z "$JAVA_HOME" ]; then
  echo "=== Java no encontrado, descargando JDK portable ==="
  cd /tmp
  
  # Descargar OpenJDK 17 portable (no requiere instalación)
  JDK_VER=17.0.10
  JDK_DIR=/tmp/jdk-$JDK_VER
  
  if [ ! -d "$JDK_DIR" ]; then
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
      # Buscar el directorio extraído (puede tener diferentes nombres)
      for dir in jdk-* jdk* java-*; do
        if [ -d "$dir" ] && [ -f "$dir/bin/java" ]; then
          mv "$dir" $JDK_DIR
          break
        fi
      done
      rm -f jdk.tar.gz
    else
      echo "Error: No se pudo descargar Java desde ninguna URL"
      exit 1
    fi
  fi
  
  if [ -d "$JDK_DIR" ]; then
    export JAVA_HOME="$JDK_DIR"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "JDK descargado en: $JAVA_HOME"
  else
    echo "Error: No se pudo descargar Java"
    exit 1
  fi
fi

echo "=== JAVA_HOME: $JAVA_HOME ==="
echo "=== Verificando Maven ==="
mvn --version

# Ir al directorio del proyecto
PROJECT_DIR="/opt/render/project/src"
if [ ! -d "$PROJECT_DIR" ]; then
  PROJECT_DIR="."
fi

cd "$PROJECT_DIR"
echo "=== Directorio del proyecto: $(pwd) ==="
ls -la

echo "=== Ejecutando Maven build ==="
mvn clean package -DskipTests

echo "=== Build completado ==="

