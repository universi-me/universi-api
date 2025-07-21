#!/bin/bash

# send the log file to the debug URL
if [ -n "$DEBUG_URL_POST" ] && [ -f "$API_DEBUG_FILE" ]; then
    # Send the file contents via POST request
    curl -k -X POST -H "Content-Type: text/plain" --data-binary @"$API_DEBUG_FILE" "$DEBUG_URL_POST"
    echo "Sent $API_DEBUG_FILE file to $DEBUG_URL_POST"
    rm -rf "$API_DEBUG_FILE"
fi

USE_RAM_PERCENTAGE="${API_USE_RAM_PERCENTAGE:-false}"

JAVA_CMD="java -XX:+UseContainerSupport"

if [ "$USE_RAM_PERCENTAGE" = "true" ]; then
    INIT_RAM_PERCENTAGE="${API_INIT_RAM_PERCENTAGE:-60}"
    MAX_RAM_PERCENTAGE="${API_MAX_RAM_PERCENTAGE:-90}"
    JAVA_CMD="$JAVA_CMD -XX:InitialRAMPercentage=$INIT_RAM_PERCENTAGE -XX:MaxRAMPercentage=$MAX_RAM_PERCENTAGE"
fi

exec $JAVA_CMD -jar /app.jar