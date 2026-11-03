@echo off
setlocal enabledelayedexpansion

for /f "delims=" %%a in (.env) do (
    set "line=%%a"
    if not "!line:~0,1!"=="#" if "!line!" neq "" (
        for /f "tokens=1,2 delims==" %%b in ("!line!") do (
            set "%%b=%%c"
        )
    )
)

REM Запускаем Spring Boot через Maven или Gradle
.\gradlew bootRun

endlocal