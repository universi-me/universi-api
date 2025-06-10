#!/bin/bash

# send the log file to the debug URL
if [ -n "$DEBUG_URL_POST" ] && [ -f "$API_DEBUG_FILE" ]; then
    # Send the file contents via POST request
    curl -k -X POST -H "Content-Type: text/plain" --data-binary @"$API_DEBUG_FILE" "$DEBUG_URL_POST"
    echo "Sent $API_DEBUG_FILE file to $DEBUG_URL_POST"
    rm -rf "$API_DEBUG_FILE"
fi

# Execute the Java application
exec java -Xms128m -Xmx512m -XX:+UseSerialGC -jar /app.jar