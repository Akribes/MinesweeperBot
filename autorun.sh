#!/usr/bin/env bash
while :
do
    git pull
    mvn install
    java -jar "Minesweeper Bot.jar"
    echo "Press Ctrl+C now to stop! Program will start in 10 seconds."
    sleep 10
done