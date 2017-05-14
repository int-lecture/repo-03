#!/bin/bash
git -c /usr/local/source/repo-03/ pull
sudo cp *.service /etc/systemd/sytem/*.service
sudo systemctl daemon-reload
echo "Services updated."