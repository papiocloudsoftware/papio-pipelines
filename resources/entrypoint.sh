#!/bin/bash

# TODO: Support configurable jenkins user/home
chown jenkins:jenkins /var
chown -R jenkins:jenkins /var/jenkins_home

su jenkins

mkdir -p /var/jenkins_home/plugins

# Doing this makes startup much faster, Java is much slower at this. Not 100% future proof though...
# For each plugin, if it isn't in the plugins dir already, move it in along with expanded dir
cd /usr/share/jenkins/ref/plugins

for archive in `ls *.jpi`; do
  dirName=${archive%.jpi}
  if [ ! -f /var/jenkins_home/plugins/$archive ]; then
    rsync -az $archive /var/jenkins_home/plugins/
    rsync -az $dirName /var/jenkins_home/plugins/
  fi
  # Move files so jenkins start script doesn't attempt to copy
  rm -rf $archive $dirName
done

cd -

/usr/local/bin/jenkins.sh
