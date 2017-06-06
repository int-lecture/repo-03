#!/bin/bash
git pull
(cd common/; mvn clean compile install)
(cd login-server/; mvn clean compile test install assembly:single)
(cd chat-server/; mvn clean compile test install assembly:single)
(cd register-server/; mvn clean compile install test assembly:single)

echo "Binaries finished building."
echo "Deploying."
rm login-server*.jar
rm chat-server*.jar
rm register-server*.jar
cp login-server/target/login-server*.jar .
cp chat-server/target/chat-server*.jar .
cp register-server/target/register-server*.jar .
mv login-server*.jar login-server.jar
mv chat-server*.jar chat-server.jar
mv register-server*.jar register-server.jar
scp *.jar docker-03:/usr/local/source/repo-03/services/
scp *.service docker-03:/usr/local/source/repo-03/services/
echo "Deployed."