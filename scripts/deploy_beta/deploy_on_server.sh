#!/bin/bash
export JAVA_HOME PATH CLASSPATH
JAVA_HOME=/soft/jdk1.8.0_91
PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/jre/lib:$CLASSPATH
nohup java -jar /data/tomcats/zhihu/com.nerc.zhihu-0.0.1-SNAPSHOT.jar --spring.profiles.active=beta &
exit