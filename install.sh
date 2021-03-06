#!/bin/bash
#
#      Filename: install.sh
#

INSTALL_DIR=/opt/scan
GBWHttpLog_DIR=GBWHttpLog
echo "Start to install GBWHttpLog..."

mkdir -p  $INSTALL_DIR
mkdir -p /opt/log/scan
chmod 777 /opt/log/scan
mkdir -p /opt/data/httpServer/
mkdir -p /opt/data/weblogic

rm -rf /opt/scan/GBWHttpLog
mv -f $GBWHttpLog_DIR $INSTALL_DIR/
chmod 755 /opt/scan/GBWHttpLog
chmod a+xwr /opt/scan -R
chown server:server /opt/scan -R
chown server:server /opt/data/httpServer -R
chown server:server /opt/data/weblogic -R

chmod a+xrw /opt/data/httpServer -R
chmod a+xrw /opt/data/weblogic -R

echo "Install GBWHttpLog done..."
