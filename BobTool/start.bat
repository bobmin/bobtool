@echo off
set GIT_WORK_PATH=D:\Eclipse\git\bobtool
set BOB_TOOL_PATH=%GIT_WORK_PATH%\BobTool\bin
set BOB_DEMO_PATH=%GIT_WORK_PATH%\BobDemo\bin
set BBMC_TOOL_PATH=%GIT_WORK_PATH%\BbmcTool\bin
REM Auswahl Konfiguration
if "%1" == "bbmc" (
	echo Konfiguration BBMC 
	set START_CLASS_PATH=%BOB_TOOL_PATH%;%BBMC_TOOL_PATH%
) else (
	echo Konfiguration DEMO
	set START_CLASS_PATH=%BOB_TOOL_PATH%;%BOB_DEMO_PATH%
)
REM Starten!
echo BobTool-STARTER...
echo [%1] classpath: %START_CLASS_PATH%
java -cp "%START_CLASS_PATH%" bob.tool.BtMain
 