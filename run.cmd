@echo off
cd /d "%~dp0"
chcp 1251 >nul
java -cp target\classes ua.edu.sumdu.j2se.clothes.ClothesDriver
