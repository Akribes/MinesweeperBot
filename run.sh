#!/usr/bin/env bash
cd $1
git pull
mvn install
java -jar "Minesweeper Bot.jar"