@echo off
REM selftest.bat: build ulang lalu jalankan SelfTest

cd /d "%~dp0"

REM call biar balik ke file ini setelah build selesai
call "%~dp0build.bat"
if errorlevel 1 exit /b 1

REM -ea biar assert aktif
echo.
echo ========== SelfTest ==========
java -ea -cp out SelfTest
