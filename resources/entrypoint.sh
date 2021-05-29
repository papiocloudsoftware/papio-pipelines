#!/bin/bash

function md5 {
  md5sum "$1" | awk '{print $1}'
}

# TODO: Support configurable jenkins user/home
chown jenkins:jenkins /var
chown -R jenkins:jenkins /var/jenkins_home

su jenkins

mkdir -p /var/jenkins_home/plugins

# Doing this makes startup much faster, Java is much slower at this. Not 100% future proof though...
# For each plugin, if it isn't in the plugins dir already, move it in along with expanded dir
cd /usr/share/jenkins/ref/plugins

{ time {
for archive in `ls *.jpi`; do
  destDir="/var/jenkins_home/plugins"
  destFile="${destDir}/${archive}"
  dirName=${archive%.jpi}
  if [ ! -f $destFile ] || [ `md5 $archive` != `md5 $destFile` ]; then
    echo "Synchronizing plugin ${archive}"...
    rsync -az $archive "${destDir}/"
    rsync -az --delete $dirName "${destDir}/"
  fi
  # Move files so jenkins start script doesn't attempt to copy
  rm -rf $archive $dirName
done
} }

cd -

/usr/local/bin/jenkins.sh
