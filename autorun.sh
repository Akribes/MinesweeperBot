#!/usr/bin/env bash
mvn package
while :
do
    git pull
    java -jar target/
    echo "Press Ctrl+C now to stop! Program will start in 10 seconds."
    sleep 10
done