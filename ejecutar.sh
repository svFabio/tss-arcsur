#!/usr/bin/env bash
set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "Iniciando aplicacion tss-arcsur..."
if [ -f "Programa Java.jar" ]; then
    java -jar "Programa Java.jar"
elif [ -f "bin/Controlador/Controlador.class" ]; then
    java -cp bin Controlador.Controlador
else
    echo "Compilando codigo fuente..."
    javac -encoding UTF-8 -d bin src/Modelo/*.java src/Vista/*.java src/Controlador/*.java
    java -cp bin Controlador.Controlador
fi
