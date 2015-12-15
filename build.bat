@ECHO OFF

IF "%JAVA_HOME%"=="" GOTO ERROR_JAVA_HOME

SET ANT_HOME=lib/ant


"%JAVA_HOME%\bin\java.exe" -Xmx512M -XX:MaxPermSize=256M -classpath %ANT_HOME%/ant-launcher.jar -Dant.home="%ANT_HOME%" -Dant.library.dir="%ANT_HOME%" org.apache.tools.ant.launch.Launcher -f build.xml %*

GOTO END


:ERROR_JAVA_HOME

ECHO ERROR: JAVA_HOME could not be found in your system environment
ECHO please set the JAVA_HOME variable in your environment to match
ECHO the location of the Java Virtual Machine you want to use


:END
