#!/bin/bash
# 智能体服务启动脚本（classpath 方式启动，conf 外置优先）
# @author viscen(徐文程) 2026年09月12日
APP_HOME=$(cd "$(dirname "$0")/.." && pwd)
CONF_DIR=$APP_HOME/conf
LIB_DIR=$APP_HOME/lib
MAIN_CLASS=com.jmn.agent.RagApplication

java -XX:+HeapDumpOnOutOfMemoryError -Xms64m -Xmx512m \
	-Djava.io.tmpdir=/tmp \
	-classpath $CONF_DIR:$LIB_DIR/* \
	$MAIN_CLASS >/dev/null 2>&1 &

echo "jmn-rag started"
