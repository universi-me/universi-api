#!/bin/bash

# send the log file to the debug URL
if [ -n "$DEBUG_URL_POST" ] && [ -f "$API_DEBUG_FILE" ]; then
    # Send the file contents via POST request
    curl -k -X POST -H "Content-Type: text/plain" --data-binary @"$API_DEBUG_FILE" "$DEBUG_URL_POST"
    echo "Sent $API_DEBUG_FILE file to $DEBUG_URL_POST"
    rm -rf "$API_DEBUG_FILE"
fi

# Execute the Java application
exec java -XX:+UseContainerSupport -XX:InitialRAMPercentage=20 -XX:MaxRAMPercentage=80 -jar /app.jar