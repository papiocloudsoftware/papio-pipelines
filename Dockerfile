ARG JENKINS_VERSION=lts
FROM jenkins/jenkins:${JENKINS_VERSION}-alpine

COPY resources /resources

RUN install-plugins.sh < /resources/plugins.txt \
  && cd /usr/share/jenkins/ref/plugins \
  && for file in `ls *.jpi`; do dirName="${file%.jpi}" && unzip $file -d $dirName && touch -a -m -t `date -r $file "+%Y%m%d%H%M.%S"` $dirName/.timestamp2 ; done

USER root

RUN apk add --no-cache rsync

ENTRYPOINT ["/sbin/tini", "--", "/resources/entrypoint.sh"]

