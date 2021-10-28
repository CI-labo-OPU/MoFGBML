@echo off

set SEED=%1
set FILENAME=%2
set DATANAME=%3
set REPEAT=%4
set CV=%5

Java -jar CrossValidation_for_CILABformat.jar %SEED% %FILENAME% %DATANAME% %REPEAT% %CV%
