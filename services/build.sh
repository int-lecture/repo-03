#!/bin/bash
git pull
(cd common/; mvn clean compile install)
(cd login-server/; mvn clean compile test install assembly:single && sudo docker build -t loginserver .)
(cd chat-server/; mvn clean compile test install assembly:single && sudo docker build -t chatserver .)
(cd register-server/; mvn clean compile install test assembly:single && sudo docker build -t registerserver .)
sudo docker tag loginserver d3adlysurprise/loginserver
sudo docker tag loginserver d3adlysurprise/registerserver
sudo docker tag loginserver d3adlysurprise/chatserver
echo "Uploading docker images."
sudo docker push d3adlysurprise/loginserver
sudo docker push d3adlysurprise/registerserver
sudo docker push d3adlysurprise/chatserver