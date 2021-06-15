#!/bin/bash

# TODO: Support configurable jenkins user/home
chown jenkins:jenkins /var
chown -R jenkins:jenkins /var/jenkins_home

# Run the custom startup script as jenkins user
su -c "/resources/startup.sh" -m jenkins
