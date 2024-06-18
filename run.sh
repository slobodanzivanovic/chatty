#!/bin/bash

start_server() {
  javac -cp ./bin -d ./bin src/main/java/com/slobodan/server/*.java
  java -cp ./bin com.slobodan.server.ChattyServer 8989
}

start_client() {
  javac -cp ./bin -d ./bin src/main/java/com/slobodan/client/*.java
  java -cp ./bin com.slobodan.client.ChattyClient localhost 8989
}

if [ "$1" == "server" ]; then
  start_server
elif [ "$1" == "client" ]; then
  start_client
else
  echo "Usage: $0 (server|client)"
  exit 1
fi