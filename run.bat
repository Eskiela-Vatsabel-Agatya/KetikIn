@echo off
REM run.bat: jalankan KetikIn dari dist\KetikIn.jar

cd /d "%~dp0"

REM jar belum ada, suruh build dulu
if not exist dist\KetikIn.jar (
    echo dist\KetikIn.jar belum ada. Jalankan build.bat dulu.
    exit /b 1
)

REM -ea biar assert ikut dicek
java -ea -jar dist\KetikIn.jar
