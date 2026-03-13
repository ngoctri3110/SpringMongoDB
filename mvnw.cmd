@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@if "%DEBUG%"=="" @echo off
@setlocal

set ERROR_CODE=0

:init
@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%\.mvn" goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd /d "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd /d "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto readMavenProjectVersion

setlocal
for /f "usebackq delims=" %%i in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%i
endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%

:readMavenProjectVersion
for /f "usebackq tokens=*" %%i in ("%MAVEN_PROJECTBASEDIR%\.mvn\maven.config") do set MAVEN_CMD_LINE_ARGS=!MAVEN_CMD_LINE_ARGS! %%i

set MAVEN_CMD_LINE=%MAVEN_CMD_LINE_ARGS%

setlocal
for /F "usebackq delims=" %%a in ('powershell -Command "& '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar'" %MAVEN_PROJECTBASEDIR%') do set CLASSWORLDS_JAR=%%a
setlocal enabledelayedexpansion
set CLASSWORLDS_JAR=!CLASSWORLDS_JAR:%MAVEN_PROJECTBASEDIR%=%%cd%%!
endlocal & endlocal
set CLASSWORLDS_LAUNCHER=org.codehaus.plexus.classworlds.launcher.Launcher
"%JAVA_HOME%\bin\java.exe" %JVM_CONFIG_MAVEN_PROPS% -classpath %CLASSWORLDS_JAR% -Dclassworlds.conf="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\m2.conf" "-Dmaven.home=%MAVEN_HOME%" "-Dlibrary.jansi.path=%MAVEN_HOME%\lib\jansi-native" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %MAVEN_CMD_LINE_ARGS% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & exit /B %ERROR_CODE%
