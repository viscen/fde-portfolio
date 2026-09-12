#!/bin/bash
# 智能体服务重启脚本
# @author viscen(徐文程) 2026年09月12日
DIR=$(cd "$(dirname "$0")" && pwd)
$DIR/stop.sh
sleep 2
$DIR/start.sh
