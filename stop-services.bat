@echo off
echo Stopping Java services...

taskkill /F /FI "IMAGENAME eq java.exe" /FI "WINDOWTITLE eq cmd*"

echo Services stopped! 