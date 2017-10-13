@REM
@REM Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
@REM
@REM This Source Code Form is subject to the terms of the Mozilla Public
@REM License, v. 2.0. If a copy of the MPL was not distributed with this
@REM file, You can obtain one at http://mozilla.org/MPL/2.0/.
@REM

REM Simple batch file that invokes launch4j and builds an exe of the users game.
@echo off
"jre\bin\java.exe" -jar "launch4j\launch4j.jar" launch4j-config.xml
if ERRORLEVEL 1 echo Error
exit