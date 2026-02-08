#!/bin/bash
set -e

echo "=== Iniciando build ==="

# Verificar si estamos en Render
if [ -d "/opt/render/project/src" ]; then
    PROJECT_DIR="/opt/render/project/src"
else
    PROJECT_DIR="."
fi

cd "$PROJECT_DIR"

echo "=== Directorio actual: $(pwd) ==="
echo "=== Contenido del directorio ==="
ls -la

# Verificar si Maven está disponible
if command -v mvn &> /dev/null; then
    echo "=== Maven encontrado en el sistema ==="
    mvn --version
    mvn clean package -DskipTests
else
    echo "=== Maven no encontrado, descargando... ==="
    
    # Descargar Maven a un directorio temporal
    MAVEN_VERSION=3.9.6
    MAVEN_DIR="/tmp/apache-maven-${MAVEN_VERSION}"
    
    if [ ! -d "$MAVEN_DIR" ]; then
        cd /tmp
        wget -q https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
        tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz
    fi
    
    export PATH="${MAVEN_DIR}/bin:$PATH"
    export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}
    
    echo "=== Maven descargado, versión: ==="
    mvn --version
    
    cd "$PROJECT_DIR"
    echo "=== Ejecutando Maven build ==="
    mvn clean package -DskipTests
fi

echo "=== Build completado ==="

