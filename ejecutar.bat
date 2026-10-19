@echo off
cd /d "%~dp0"
echo Iniciando aplicacion tss-arcsur...
if exist "bin\Controlador\Controlador.class" (
    start javaw -cp bin Controlador.Controlador
) else (
    echo Compilando codigo fuente...
    javac -encoding UTF-8 -d bin src\Modelo\*.java src\Vista\*.java src\Controlador\*.java
    start javaw -cp bin Controlador.Controlador
)
