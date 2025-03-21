@echo off
echo Cleaning conflicting OpenAPI configuration classes...
del /F /Q target\classes\com\hasandag\courier\tracking\system\location\config\OpenApiConfig.class 2>nul
del /F /Q target\classes\com\hasandag\courier\tracking\system\location\config\SwaggerConfig.class 2>nul
del /F /Q target\classes\com\hasandag\courier\tracking\system\location\config\SwaggerUIConfig.class 2>nul

echo Creating config directory for cleaned classes...
mkdir target\classes\com\hasandag\courier\tracking\system\location\config 2>nul

echo Copying updated source classes...
copy src\main\java\com\hasandag\courier\tracking\system\location\config\OpenApiConfig.java target\classes\com\hasandag\courier\tracking\system\location\config\ 2>nul
copy src\main\java\com\hasandag\courier\tracking\system\location\config\SwaggerUIConfig.java target\classes\com\hasandag\courier\tracking\system\location\config\ 2>nul

echo Starting Location Service with bean definition overriding enabled...
cd ..
java -jar location-service/target/location-service-0.0.1-SNAPSHOT.jar --spring.main.allow-bean-definition-overriding=true --logging.level.net.logstash=OFF 