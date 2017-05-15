#!/bin/bash
git pull
(cd chat-server/; mvn clean compile assembly:single)
(cd login-server/; mvn clean compile assembly:single)
echo "Binaries finished building."
echo "Deploying."
mv login-server/target/login-server*.jar login-server.jar
mv chat-server/target/chat-server*.jar chat-server.jar
scp *.jar docker-03:/usr/local/source/repo-03/services/
echo "Deployed."