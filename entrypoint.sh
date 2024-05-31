#!/bin/bash

# send the log file to the debug URL
if [ -n "$DEBUG_URL_POST" ] && [ -f "/spring.log" ]; then
    # Send the file contents via POST request
    curl -X POST -H "Content-Type: text/plain" --data-binary @/spring.log "$DEBUG_URL_POST"
    echo "Sent /spring.log file to $DEBUG_URL_POST"
    rm -rf /spring.log
fi

# Execute the Java application
exec java -jar /app.jar