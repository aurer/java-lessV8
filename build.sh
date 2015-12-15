#!/bin/bash

java -classpath lib/ant/ant-launcher.jar -Dant.home=lib/ant org.apache.tools.ant.launch.Launcher -f build.xml $*
