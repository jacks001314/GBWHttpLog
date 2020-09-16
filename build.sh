#!/bin/bash

source /etc/profile
GBWHttpLog_SRC=GBWHttpLog
GBWHttpLog_TAR=GBWHttpLog-1.0-bin.tar.gz
GBWHttpLog_DIST=target/GBWHttpLog-1.0-bin.tar.gz
GBWHttpLog_JAR=target/GBWHttpLog-1.0.jar

rm -rf build target

mkdir build

mvn clean
mvn install -Dmaven.test.skip=true

cp $GBWHttpLog_DIST build/
cd build
tar -zxf $GBWHttpLog_TAR
cp ../$GBWHttpLog_JAR $GBWHttpLog_SRC/lib
cp ../install.sh ./
chmod a+x *.sh
rm -fr $GBWHttpLog_TAR

cd ../
rm -rf target
echo ""
echo "build done..."
