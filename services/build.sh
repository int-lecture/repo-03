#!/bin/bash
git pull
./chat-server/build.sh
./login-server/build.sh
echo "Binaries finished building."
echo "Deploying."
mv login-server/target/login-server*.jar login-server.jar
mv chat-server/target/chat-server*.jar chat-server.jar
scp *.jar docker-03:/usr/local/source/repo-03/services/target/
echo "Deployed."