#!/bin/bash
# 智能体服务停止脚本
# @author viscen(徐文程) 2026年09月12日
PID=$(ps -ef | grep AgentApplication | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
	kill -9 $PID
	echo "jmn-agent stopped (pid=$PID)"
else
	echo "jmn-agent not running"
fi
