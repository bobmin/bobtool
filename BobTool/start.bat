@echo off
set GIT_WORK_PATH=D:\Eclipse\git\bobtool
set BOB_TOOL_PATH=%GIT_WORK_PATH%\BobTool\bin
set BBMC_TOOL_PATH=%GIT_WORK_PATH%\BbmcTool\bin
set START_CLASS_PATH=%BOB_TOOL_PATH%;%BBMC_TOOL_PATH%
echo BobTool-STARTER...
echo classpath: %START_CLASS_PATH%
java -cp "%START_CLASS_PATH%" bob.tool.BtMain
 