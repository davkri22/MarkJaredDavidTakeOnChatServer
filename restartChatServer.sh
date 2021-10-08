#!/bin/sh
# A simple example of a script that will restart the server every time it crashes
# It will also log the crashes and restarts


# set executable permissions with:
#   chmod +x chatServerRestartScript.sh

# execute with:
#   nohup ./restartChatServer.sh &

while true 
do
# Run the server (again). Append to both log files
echo "Server (re)started at: $(date)" >> chatserver_restarts.log
echo "Server (re)started at: $(date)" >> chatserver.log

# Use >> to append, not overwrite log
java src/ChatServer &>> chatserver.log
# same as java ChatServer >> chatserver.log 2 > &1

# If crashes, we end up here
echo "Server crashed at: $(date)" >> chatserver_restarts.log 
echo "Server crashed at: $(date)" >> chatserver.log

sleep 10 # if your server has something wrong with it on startup, keeps from overwhelming the server
done