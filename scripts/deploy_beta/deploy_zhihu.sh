#!/bin/bash
datetag=$1
if [ "$datetag" == "" ]; then
    datetag=$(date +%Y%m%d%H%M)
fi
sh ./scripts/deploy_beta/deploy_cp2producttransfer.sh 172.16.20.6 com.nerc.zhihu-0.0.1-SNAPSHOT.jar $datetag