@echo off
REM ==================================================================
REM  build.bat - compile KetikIn lalu buat dist\KetikIn.jar
REM  Cara pakai: buka terminal di folder project, ketik  build.bat
REM ==================================================================

REM Pindah ke folder tempat file .bat ini berada, supaya bisa dijalankan dari mana saja.
cd /d "%~dp0"

REM Langkah 1: hapus folder out dan dist yang lama, lalu buat lagi yang kosong.
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist

REM Langkah 2: compile semua file .java (satu baris per package) ke folder out.
REM   --release 8      = hasil compile bisa dijalankan di Java 8 ke atas
REM   -Xlint:-options  = sembunyikan peringatan "release 8 sudah tua" dari javac baru
REM   -encoding UTF-8  = file sumber kita disimpan sebagai UTF-8
REM   -d out           = taruh file .class di folder out
echo [1/3] Compile...
javac --release 8 -Xlint:-options -encoding UTF-8 -d out src\app\*.java src\model\*.java src\mode\*.java src\data\*.java src\engine\*.java src\ui\*.java test\*.java
if errorlevel 1 goto failed

REM Langkah 3: salin file kata dan potongan kode ke out\resources, supaya ikut masuk jar.
echo [2/3] Salin resources...
xcopy /e /i /q /y src\resources out\resources > nul
if errorlevel 1 goto failed

REM Langkah 4: bungkus isi folder out menjadi satu file jar.
REM   c = create (buat jar baru), f = nama file jar, e = entry point (kelas yang punya main)
REM   -C out . = ambil semua isi folder out
echo [3/3] Buat jar...
jar cfe dist\KetikIn.jar app.Main -C out .
if errorlevel 1 goto failed

REM Langkah 5: tampilkan pesan berhasil atau gagal.
echo.
echo BUILD BERHASIL: dist\KetikIn.jar sudah dibuat.
echo Jalankan dengan: run.bat
exit /b 0

:failed
echo.
echo BUILD GAGAL. Lihat pesan error di atas.
exit /b 1
