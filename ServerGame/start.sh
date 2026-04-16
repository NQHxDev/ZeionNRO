#!/bin/bash

# Configurations
JAR_FILE="ServerGame.jar"
JAVA_OPTS="-Xms1G -Xmx2G -Djava.awt.headless=true"
RESTART_DELAY=5

echo "----------------------------------------------------"
echo "ZEION NRO - Server Startup Script Started ..."
echo "----------------------------------------------------"

while true; do
   echo ">>> Launching Game Server ..."
   java $JAVA_OPTS -jar $JAR_FILE

   EXIT_CODE=$?
   echo ">>> Game Server stopped with exit code: $EXIT_CODE"

   if [ $EXIT_CODE -eq 0 ]; then
      echo ">>> Server shut down normally (Maintenance)! Preparing to restart ..."
   else
      echo ">>> Server crashed or encountered an error! Restarting ..."
   fi

   echo ">>> Rebooting in $RESTART_DELAY seconds ..."
   sleep $RESTART_DELAY
done
