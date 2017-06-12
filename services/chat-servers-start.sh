#!/bin/bash
cd /usr/local/source/repo-03/services
sudo nohup java -jar chat-server.jar -baseURI http://0.0.0.0:4000/&
sudo nohup java -jar chat-server.jar -baseURI http://0.0.0.0:4001/&
sudo nohup java -jar chat-server.jar -baseURI http://0.0.0.0:4002/&
echo "Chat Servers started."