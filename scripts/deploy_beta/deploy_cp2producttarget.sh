#!/bin/bash

targetserver=$1
model=$2
datetag=$3
newname=$4
ssh_transfer=root@$targetserver
find /deploy/wars/ -name *com.nerc.zhihu* | grep -v $datetag | xargs rm -rf
echo "2"
file=$(ls /deploy/wars/$newname)
filename=${file##*/}
echo "/deploy/wars/$model"

#复制到生产服务器
eval `ssh-agent -s`
ssh-add
if ssh $ssh_transfer test -e /deploy/; then
    echo "/deploy/ exists"
else
    ssh -o "StrictHostKeyChecking no" $transferserver 'mkdir -p /deploy/nerc-scripts/ /deploy/wars/'
fi
#echo "$filename"
scp -o "StrictHostKeyChecking no" /deploy/wars/$filename $targetserver:/deploy/wars/$model

ssh -o "StrictHostKeyChecking no" $targetserver 'rm -rf /deploy/nerc-scripts/deploy_target.sh'
ssh -o "StrictHostKeyChecking no" $targetserver 'rm -rf /deploy/nerc-scripts/deploy_on_server.sh'
scp -o "StrictHostKeyChecking no" /deploy/nerc-scripts/deploy_target.sh $targetserver:/deploy/nerc-scripts/
scp -o "StrictHostKeyChecking no" /deploy/nerc-scripts/deploy_on_server.sh $targetserver:/deploy/nerc-scripts/
ssh -o "StrictHostKeyChecking no" $targetserver sh /deploy/nerc-scripts/deploy_target.sh $model $datetag
