@echo off

REM Lấy thư mục chứa file .cmd
set BASE_DIR=%~dp0

start "" powershell.exe ^
  -NoProfile ^
  -STA ^
  -WindowStyle Hidden ^
  -ExecutionPolicy Bypass ^
  -File "%BASE_DIR%antiddos_gui.ps1"

exit
