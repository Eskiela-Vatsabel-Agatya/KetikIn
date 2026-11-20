@echo off
REM ==================================================================
REM  selftest.bat - build ulang, lalu jalankan SelfTest
REM ==================================================================

REM Pindah ke folder tempat file .bat ini berada.
cd /d "%~dp0"

REM Build dulu, supaya folder out berisi kode terbaru.
REM "call" dipakai supaya setelah build.bat selesai, file ini lanjut ke baris berikutnya.
REM %~dp0 = folder file .bat ini, supaya build.bat pasti ketemu.
call "%~dp0build.bat"
if errorlevel 1 exit /b 1

REM Jalankan SelfTest dari folder out. -ea = aktifkan assert.
echo.
echo ========== SelfTest ==========
java -ea -cp out SelfTest
