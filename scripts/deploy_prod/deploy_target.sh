#!/bin/bash
model=$1
datetag=$2
#export JAVA_HOME=/soft/jdk1.8.0_91
model_pid=$(ps -ef|grep $model|grep -v grep|grep -v deploy_target |head -1 |awk '{print $2}');
echo "id is$model_pid"
[ -n "$model_pid" ] && kill -9 $model_pid
cd /data/tomcats/zhihu/
#mv  ROOT.jar ROOT_jar.bak
rm -rf ROOT.war
rm -rf ROOT
cp -f /deploy/wars/$model $model
#$nohub java -jar /data/tomcats/zhihu/com.nerc.zhihu-0.0.1-SNAPSHOT.jar --spring.profiles.active=beta &
nohup sh /deploy/nerc-scripts/deploy_on_server.sh

