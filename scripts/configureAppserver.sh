#!/bin/bash
set -e
mkdir /opt/vexpress-scheduling
chown $1 /opt/vexpress-scheduling
cd /opt/vexpress-scheduling
mv /tmp/application.properties .
wget --auth-no-challenge --user=$2 --password=$3 $4/artifact/build/libs/vexpress-scheduling-$5.jar -O vexpress-scheduling.jar
mv /tmp/vexpress-scheduling.service /etc/systemd/system
chmod 664 /etc/systemd/system/vexpress-scheduling.service

# Wait until Java is installed
until [ -f /usr/bin/java ]; do
  sleep 10
done

systemctl enable vexpress-scheduling
systemctl start vexpress-scheduling
