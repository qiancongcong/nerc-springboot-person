#!/bin/bash
cp -rf ./target/com.nerc.zhihu-0.0.1-SNAPSHOT.jar /opt/apache-tomcat-7.0.22/webapps/ROOT/wars/
eval `ssh-agent -s`
ssh-add
ssh -o "StrictHostKeyChecking no" 192.168.60.142 'rm -rf /tmp/nerc-scripts;mkdir /tmp/nerc-scripts'
scp -o "StrictHostKeyChecking no" scripts/deploy_test/deploy_on_server.sh 192.168.60.142:/tmp/nerc-scripts/deploy_on_server.sh
scp -o "StrictHostKeyChecking no" scripts/deploy_test/deploy_target.sh 192.168.60.142:/tmp/nerc-scripts/deploy.sh
ssh -o "StrictHostKeyChecking no" 192.168.60.142 sh /tmp/nerc-scripts/deploy.sh com.nerc.zhihu-0.0.1-SNAPSHOT.jar
