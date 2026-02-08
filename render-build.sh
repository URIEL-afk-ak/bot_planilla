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

