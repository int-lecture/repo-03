#!/bin/bash
git -C /usr/local/source/repo-03/ pull
sudo cp *.service /etc/systemd/system/
sudo systemctl daemon-reload
echo "Services updated."