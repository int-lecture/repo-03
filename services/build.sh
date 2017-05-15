#!/bin/bash
git pull
./chat-server/build.sh
./login-server/build.sh
echo "Binaries finished building."
echo "Deploying."
mv login-server/target/login-server*.jar login-server.jar
mv chat-server/target/chat-server*.jar chat-server.jar
echo "Deployed."