@echo off
REM ==================================================================
REM  run.bat - menjalankan KetikIn dari dist\KetikIn.jar
REM ==================================================================

REM Pindah ke folder tempat file .bat ini berada.
cd /d "%~dp0"

REM Kalau jar belum ada, minta build dulu.
if not exist dist\KetikIn.jar (
    echo dist\KetikIn.jar belum ada. Jalankan build.bat dulu.
    exit /b 1
)

REM -ea = enable assertions: perintah assert di kode ikut dicek (materi Exception & Assertion).
java -ea -jar dist\KetikIn.jar
