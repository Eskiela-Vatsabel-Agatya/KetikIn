@echo off
REM build.bat: compile KetikIn lalu buat dist\KetikIn.jar

REM pindah ke folder file ini biar bisa dijalankan dari mana saja
cd /d "%~dp0"

REM bersihkan out dan dist lama
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist

REM compile semua package, --release 8 biar jalan di Java 8 ke atas
echo [1/3] Compile...
javac --release 8 -Xlint:-options -encoding UTF-8 -d out src\app\*.java src\model\*.java src\mode\*.java src\data\*.java src\engine\*.java src\ui\*.java test\*.java
if errorlevel 1 goto failed

REM salin file kata biar ikut masuk jar
echo [2/3] Salin resources...
xcopy /e /i /q /y src\resources out\resources > nul
if errorlevel 1 goto failed

REM bungkus jadi jar dengan entry point app.Main
echo [3/3] Buat jar...
jar cfe dist\KetikIn.jar app.Main -C out .
if errorlevel 1 goto failed

echo.
echo BUILD BERHASIL: dist\KetikIn.jar sudah dibuat.
echo Jalankan dengan: run.bat
exit /b 0

:failed
echo.
echo BUILD GAGAL. Lihat pesan error di atas.
exit /b 1
