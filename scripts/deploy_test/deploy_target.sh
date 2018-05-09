#!/bin/bash
model=$1
model_pid=$(ps -ef|grep com.nerc.zhihu-0.0.1-SNAPSHOT.jar|grep -v grep|grep -v deploy_on_server |head -1|awk '{print $2}' );
echo "id is$model_pid"
[ -n "$model_pid" ] && kill -9 $model_pid
cd /soft/zhihu
mv $1 $1.bak
rm -fr $1
wget http://192.168.60.59:8080/wars/$model
nohup sh /tmp/nerc-scripts/deploy_on_server.sh

