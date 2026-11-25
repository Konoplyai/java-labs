@echo off
cd /d "%~dp0"
chcp 1251 >nul
java -cp "target\classes;target\lib\*" ua.edu.sumdu.j2se.clothes.ClothesDriver json
