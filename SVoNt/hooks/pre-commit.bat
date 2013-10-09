@ECHO OFF

:: Pre-Commit
set repository=%1
set txn=%2

::Base Paths
set java="java.exe"
set svontRoot=d:\svont\
set tempDir=\temp\
set hookjar=pchook.jar

::TempFiles
set errorResultFile=errorResult.txt

::Filepaths
set svontJarPath="%svontRoot%%hookjar%"
set errorResultTmpPath="%repository%%tempDir%%errorResultFile%"

::first test if temp directory exists
dir /b /ad "%repository%%tempDir%" >nul 2>nul && GOTO dirExists
echo "Temp directory does not exist -- creating one..." >&2
mkdir "%repository%%tempDir%"
:dirExists


:: Start Java svont processing
::Arguments
:: Repository Directory
:: svont RootDirectory
:: Transaction Number
:: Temp Directory
:: Error Result File

%java% -jar  %svontJarPath% %1 %svontRoot%  %2 %tempDir% %errorResultFile%  >&2

:: cgeck error file
FindStr [a-zA-Z0-9] %errorResultTmpPath% >&2

::return 0 when no error detected
IF %ERRORLEVEL% EQU 0 GOTO ERROR
DEL %errorResultTmpPath%
::echo " no Error" >&2
exit 0

::error detected 
:ERROR
echo "Error" >&2
DEL %errorResultTmpPath%
exit 1

