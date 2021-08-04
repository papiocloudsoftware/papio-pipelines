#!/bin/bash

function md5 {
  md5sum "$1" | awk '{print $1}'
}

mkdir -p /var/jenkins_home/plugins

# Doing this makes startup much faster, Java is much slower at this. Not 100% future proof though...
# For each plugin, if it isn't in the plugins dir already, move it in along with expanded dir
{ time {

refDir="/usr/share/jenkins/ref/plugins"
destDir="/var/jenkins_home/plugins"

cd $refDir
for archive in `ls *.jpi`; do
  destFile="${destDir}/${archive}"
  dirName=${archive%.jpi}
  if [ ! -f $destFile ] || [ `md5 $archive` != `md5 $destFile` ]; then
    echo "Synchronizing plugin ${archive}"...
    rsync -az $archive "${destDir}/"
    rsync -az --delete $dirName "${destDir}/"
  fi
done
cd -

# Remove plugins that are no longer in reference location
cd $destDir
for archive in `ls *.jpi`; do
  refFile="${refDir}/${archive}"
  dirName=${archive%.jpi}
  refArchiveDir="${refDir}/${dirName}"
  if [ ! -f $refFile ] && [ ! -d $refArchiveDir ]; then
    echo "Removing old plugin ${archive}..."
    rm -rf $archive $dirName
  fi
done
# Delete image reference plugins so jenkins start script doesn't attempt to sync them
rm -rf $refDir
cd -

} }

# Ensure proper ownership of all /var/jenkins_home
# TODO: Support configurable jenkins user/home
chown jenkins:jenkins /var
chown -R jenkins:jenkins /var/jenkins_home

# Run jenkins.sh as the jenkins user
su -c "/usr/local/bin/jenkins.sh" -m jenkins
