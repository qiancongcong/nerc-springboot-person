#!/bin/bash

targetserver=$1
model=$2
datetag=$3
transferserver=120.27.228.115
echo `pwd`
echo $(ls ./target/$model)
file=$(ls ./target/$model)
filename=${file##*/}
newname=${filename%.*}-$datetag.${file##*.}
#复制到阿里云堡垒机
eval `ssh-agent -s`
ssh-add

ssh_transfer=root@120.27.228.115
transferfile=/deploy/wars/$newname
if ssh $ssh_transfer test -e $transferfile; then
    echo $file exists
else
    #中转服务器如果有了同名包就不再复制，针对同包集群部署增加效率。
    echo "copy wars and scripts"
    scp -o "StrictHostKeyChecking no" target/$model $transferserver:/deploy/wars/$newname
    echo "1"
    ssh -o "StrictHostKeyChecking no" $transferserver 'rm -rf /deploy/nerc-scripts/deploy_cp2producttarget.sh'
    ssh -o "StrictHostKeyChecking no" $transferserver 'rm -rf /deploy/nerc-scripts/deploy_target.sh'
    ssh -o "StrictHostKeyChecking no" $transferserver 'rm -rf /deploy/nerc-scripts/deploy_on_server.sh'
    scp -o "StrictHostKeyChecking no" scripts/deploy_prod/deploy_cp2producttarget.sh $transferserver:/deploy/nerc-scripts/
    scp -o "StrictHostKeyChecking no" scripts/deploy_prod/deploy_target.sh $transferserver:/deploy/nerc-scripts/
    scp -o "StrictHostKeyChecking no" scripts/deploy_prod/deploy_on_server.sh $transferserver:/deploy/nerc-scripts/

fi
ssh -o "StrictHostKeyChecking no" $transferserver sh /deploy/nerc-scripts/deploy_cp2producttarget.sh $targetserver $model $datetag $newname
